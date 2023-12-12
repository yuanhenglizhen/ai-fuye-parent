package com.hanzaitu.common.core.executor;

import com.hanzaitu.common.utils.Threads;
import com.hanzaitu.common.utils.spring.SpringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class ExecutorConfig {


    /**
     * 核心线程数10：线程池创建时候初始化的线程数
     */
    private static final Integer CORE_POOL_SIZE = 10;

    /**
     * 最大线程数15：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
     */
    private static final Integer MAX_POOL_SIZE = 15;

    /**
     * 缓冲队列25：用来缓冲执行任务的队列
     */
    private static final Integer QUEUE_CAPACITY = 25;

    /**
     * 允许线程的空闲时间200秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
     */
    private static final Integer KEEP_ALIVE = 200;


    static Logger logger = Logger.getLogger(String.valueOf(ExecutorConfig.class));

    private static ThreadPoolTaskExecutor thPoolInstance = null;

    /**
     * 线程池
     *
     * @return ThreadPoolTaskExecutor
     * @date 2021-9-25 10:58:57
     */
    public static ThreadPoolTaskExecutor getThreadPoolInstance() {
        if (thPoolInstance != null) {
            return thPoolInstance;
        }
        synchronized (ExecutorConfig.class) {
            if (thPoolInstance == null) {
                try {
                    // 获取统一线程池
                    thPoolInstance = getThPoolInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 如果统一线程池还是为空，将启动本地创建线程，进行保护。
                    if (thPoolInstance == null) {
                        thPoolInstance = getThPoolInstance();
                    }
                }
            }
        }

        return thPoolInstance;
    }

    /**
     * 获取本地线程池
     *
     * @return ThreadPoolTaskExecutor
     * @date 2021-9-25 10:58:53
     */
    private static ThreadPoolTaskExecutor getThPoolInstance() {
        /**
         * 如果对应的线程池不为空则直接返回，如果为空
         */
        if (thPoolInstance != null) {
            return thPoolInstance;
        }

        try {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(CORE_POOL_SIZE);
            executor.setMaxPoolSize(MAX_POOL_SIZE);
            executor.setQueueCapacity(QUEUE_CAPACITY);
            executor.setKeepAliveSeconds(KEEP_ALIVE);
            // 线程池名的前缀：设置好了之后可以方便定位处理任务所在的线程池
            executor.setThreadNamePrefix("hzt-");
            /**
             * 线程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy策略， 当线程池没有处理能力的时候，
             * 该策略会直接在 execute 方法的调用线程中运行被拒绝的任务； 如果执行程序已关闭，则会丢弃该任务
             */
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
            executor.setWaitForTasksToCompleteOnShutdown(true);
            // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。
            executor.setAwaitTerminationSeconds(60 * 2);
            executor.initialize();
            thPoolInstance = executor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thPoolInstance;
    }



    public void shutdown()
    {

        thPoolInstance.shutdown();
    }

}
