package cn.melancholy.service;

import cn.melancholy.entity.ScheduledJob;

public interface ScheduledTaskService{
 
    Boolean start(ScheduledJob scheduledJob);
 
    Boolean stop(String jobKey);
 
    Boolean restart(ScheduledJob scheduledJob);
 
    void initTask();
}