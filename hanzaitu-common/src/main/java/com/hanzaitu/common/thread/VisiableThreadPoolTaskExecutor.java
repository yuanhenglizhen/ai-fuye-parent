package com.hanzaitu.common.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 显示线程执行情况：任务总数、已完成数、活跃线程数，队列大小信息
 */
@Slf4j
public class VisiableThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {


    private void showThreadPoolInfo(){
        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
        if(null == threadPoolExecutor){
            return;
        }
        // 当任务的队列长度大于总长度的80%时告警
        if(threadPoolExecutor.getQueue().size() >
                (threadPoolExecutor.getQueue().size()+threadPoolExecutor.getQueue().remainingCapacity()) *0.8){
            log.warn("{},taskCount [{}], completedTaskCount [{}], activeThreadCount [{}], queueSize [{}]",
                    this.getThreadNamePrefix(),
                    threadPoolExecutor.getTaskCount(),
                    threadPoolExecutor.getCompletedTaskCount(),
                    threadPoolExecutor.getActiveCount(),
                    threadPoolExecutor.getQueue().size());
        }
    }

    @Override
    public void execute(Runnable task) {
        showThreadPoolInfo();
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        showThreadPoolInfo();
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        showThreadPoolInfo();
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        showThreadPoolInfo();
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        showThreadPoolInfo();
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        showThreadPoolInfo();
        return super.submitListenable(task);
    }
}
