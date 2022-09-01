package cn.melancholy.service;

import cn.melancholy.entity.ScheduledJob;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 作者： Juran on 2022-09-01 19:05
 * 作者博客：iit.la
 */
public interface PartnerService extends IService<ScheduledJob> {

    Boolean start(ScheduledJob scheduledJob);

    Boolean stop(String jobKey);

    Boolean restart(ScheduledJob scheduledJob);

    void initTask();
}
