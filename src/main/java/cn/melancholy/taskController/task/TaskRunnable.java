package cn.melancholy.taskController.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 作者： Juran on 2022-09-01 19:31
 * 作者博客：iit.la
 */
@Component
@Slf4j
public class TaskRunnable implements Runnable{

    @Override
    public void run() {
        log.info("任务开始执行。");
    }

}
