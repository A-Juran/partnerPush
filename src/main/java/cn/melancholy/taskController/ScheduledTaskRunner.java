package cn.melancholy.taskController;

import cn.melancholy.service.ScheduledTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//ApplicationRunner 实现项目启动就能执行的功能。
public class ScheduledTaskRunner implements ApplicationRunner {
    private final ScheduledTaskService scheduledTaskService;

    public ScheduledTaskRunner(ScheduledTaskService scheduledTaskService) {
        this.scheduledTaskService = scheduledTaskService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("----初始化定时任务开始----");
        scheduledTaskService.initTask();
        log.info("----初始化定时任务完成----");
    }
}