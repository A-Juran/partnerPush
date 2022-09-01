package cn.melancholy.taskController.task;

import cn.melancholy.config.WxConfigure;
import cn.melancholy.entity.ScheduledJob;
import cn.melancholy.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 作者： Juran on 2022-09-01 19:31
 * 作者博客：iit.la
 */
@Slf4j
@Component
public class TaskRunnable implements Runnable {

    private ScheduledJob scheduledJob;

    public TaskRunnable(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }

    /**
     * 通过该方法实现每日推送。
     */
    @Override
    public void run() {
        log.info(scheduledJob.getPushWx());
        Class<?> clazz;
        WxConfigure wxConfigure;
        try {
            //通过反射机制拿到Spring容器中的WxConfigure配置信息
            clazz = Class.forName("cn.melancholy.config.WxConfigure");
            wxConfigure = (WxConfigure) SpringContextUtil.getBean(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        log.info(wxConfigure.getApp_id());
    }

}
