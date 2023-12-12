package com.hanzaitu.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.hanzaitu.common.aop.RequestLogAop;
import com.hanzaitu.common.thread.VisiableThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Order(1)
@Slf4j
@Configuration
public class BeanConfig {


    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    /**
     * Json序列化和反序列化转换器，用于转换POST请求体中的json以及将我们的对象序列化为返回响应的json
     */
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false);

        //LocalDateTime系列序列化和反序列化模块，继承自jsr310，我们在这里修改了日期格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));

        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));

        //Date序列化和反序列化
        javaTimeModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN);
                String formattedDate = formatter.format(date);
                jsonGenerator.writeString(formattedDate);
            }
        });
        javaTimeModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN);
                String date = jsonParser.getText();
                try {
                    return format.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    @Bean
    @ConfigurationProperties(prefix = "logs")
    public RequestLogAop createRequestLogAspect(){
        RequestLogAop requestLogAspect = new RequestLogAop();
        log.info("RequestLogAspect created");
        return requestLogAspect;
    }


    public AppProperties appProperties(){
        AppProperties appProperties = new AppProperties();
        log.info("AppProperties created");
        return appProperties;
    }

    /**
     * cpu核心数
     */
    static final int nThreads = Runtime.getRuntime().availableProcessors();

    /**
     * SpringBoot会优先使用名称为"taskExecutor"的线程池。
     * 如果没有找到，才会使用其他类型为TaskExecutor或其子类的线程池。
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = AppProperties.APP_PREFIX, name = "config.enable-thread-pool")
    public Executor taskExecutor(TaskExecutionProperties properties) {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        // 配置核心线程数
        int coreSize = properties.getPool().getCoreSize();
        // 如果为spring默认配置的线程数量，则重置为cpu核心线程数*2
        if(coreSize == 8){
            coreSize = nThreads;
        }
        executor.setCorePoolSize(coreSize);
        // 设置最大线程数
        int maxSize = properties.getPool().getMaxSize();
        // 如果是默认值，则重置最大线程数量为核心线程数量的2倍
        if(maxSize == Integer.MAX_VALUE){
            maxSize = coreSize*2;
        }
        // 如果配置的核心线程数量小于最大线程数量，则重置最大线程数量为核心线程数量的2倍
        if(coreSize > maxSize){
            maxSize = coreSize*2;
        }
        executor.setMaxPoolSize(maxSize);
        // 设置队列容量
        int queueCapacity = properties.getPool().getQueueCapacity();
        if(queueCapacity == Integer.MAX_VALUE){
            queueCapacity = 100;
        }
        executor.setQueueCapacity(queueCapacity);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds((int)properties.getPool().getKeepAlive().getSeconds());
        // 是否允许线程池超时
        executor.setAllowCoreThreadTimeOut(properties.getPool().isAllowCoreThreadTimeout());
        // 配置线程池中的线程的名称前缀
        String threadNamePrefix = properties.getThreadNamePrefix();
        if(threadNamePrefix.startsWith("task-")){
            threadNamePrefix = "hzt-task-";
        }
        executor.setThreadNamePrefix(threadNamePrefix);
        // 设置拒绝策略
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 等待所有任务结束后再关闭线程池
//        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 执行初始化
        executor.initialize();
        log.info("ThreadPoolTaskExecutor created");
        return executor;
    }

    @Bean
    @ConditionalOnProperty(prefix = AppProperties.APP_PREFIX, name = "config.enable-scheduler")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(TaskSchedulingProperties taskSchedulingProperties) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        int poolSize = taskSchedulingProperties.getPool().getSize();
        if(taskSchedulingProperties.getPool().getSize() == 1){
            poolSize = nThreads;
        }
        threadPoolTaskScheduler.setPoolSize(poolSize);
        if(taskSchedulingProperties.getThreadNamePrefix().startsWith("scheduling-")){
            threadPoolTaskScheduler.setThreadNamePrefix("hzt-scheduling-");
        }else{
            threadPoolTaskScheduler.setThreadNamePrefix(taskSchedulingProperties.getThreadNamePrefix());
        }
        threadPoolTaskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        threadPoolTaskScheduler.initialize();
//        threadPoolTaskScheduler.getScheduledThreadPoolExecutor().setCorePoolSize(1);
//        threadPoolTaskScheduler.getScheduledThreadPoolExecutor().setMaximumPoolSize(1);
//        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
//        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
        log.info("ThreadPoolTaskScheduler created");
        return threadPoolTaskScheduler;
    }

}
