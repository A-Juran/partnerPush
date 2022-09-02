package cn.melancholy.service.impl;

import cn.melancholy.entity.ScheduledJob;
import cn.melancholy.service.PartnerService;
import cn.melancholy.service.ScheduledTaskService;
import cn.melancholy.taskController.task.TaskRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    /**
     * 可重入锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 定时任务线程池
     */
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 启动状态的定时任务集合
     */
    public Map<String, ScheduledFuture> scheduledFutureMap = new ConcurrentHashMap<>();

    @Autowired
    private PartnerService partnerService;

    @Override
    public Boolean start(ScheduledJob scheduledJob) {
        String jobKey = scheduledJob.getJobKey();
        log.info("启动定时任务" + jobKey);
        //添加锁放一个线程启动，防止多人启动多次
        lock.lock();
        log.info("加锁完成");

        try {
            if (this.isStart(jobKey)) {
                log.info("当前任务在启动状态中");
                return false;
            }
            //任务启动
            this.doStartTask(scheduledJob);
        } finally {
            lock.unlock();
            log.info("解锁完毕");
        }

        return true;
    }

    /**
     * 任务是否已经启动
     */
    private Boolean isStart(String taskKey) {
        //校验是否已经启动
        if (scheduledFutureMap.containsKey(taskKey)) {
            if (!scheduledFutureMap.get(taskKey).isCancelled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean stop(String jobKey) {
        log.info("停止任务 " + jobKey);
        boolean flag = scheduledFutureMap.containsKey(jobKey);
        log.info("当前实例是否存在 " + flag);
        if (flag) {
            ScheduledFuture scheduledFuture = scheduledFutureMap.get(jobKey);

            scheduledFuture.cancel(true);

            scheduledFutureMap.remove(jobKey);
        }
        return flag;
    }

    @Override
    public Boolean restart(ScheduledJob scheduledJob) {
        log.info("重启定时任务" + scheduledJob.getJobKey());
        //停止
        this.stop(scheduledJob.getJobKey());

        return this.start(scheduledJob);
    }

    /**
     * 执行启动任务
     */
    public void doStartTask(ScheduledJob sj) {
        log.info(sj.getJobKey());
        if (sj.getStatus().intValue() != 1)
            return;
        //启用定时器
        ScheduledFuture scheduledFuture = threadPoolTaskScheduler.schedule(new TaskRunnable(sj),
                (triggerContext -> new CronTrigger(sj.getCronExpression()).nextExecutionTime(triggerContext)));
        //将已经执行的线程放入concurrentMap中。
        scheduledFutureMap.put(sj.getJobKey(), scheduledFuture);
    }

    @Override
    public void initTask() {
        List<ScheduledJob> list = partnerService.list();
        for (ScheduledJob sj : list) {
            if (sj.getStatus().intValue() == -1) //未启用
                continue;
            doStartTask(sj);
        }
    }

}