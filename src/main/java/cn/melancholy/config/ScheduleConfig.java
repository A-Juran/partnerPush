package cn.melancholy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 作者： Juran on 2022-09-01 19:12
 * 作者博客：iit.la
 */
@Component
@Slf4j
public class ScheduleConfig {
    /**
     * 创建ScheduleConfig线程池
     * 用该线程池分别处理不同时间或者同一时间的任务执行。
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){

        log.info("线程池创建");
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        //设置线程池数量
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.setThreadNamePrefix("partnerPush-");
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
        log.info("线程池创建成功");
        return threadPoolTaskScheduler;
    }

}
