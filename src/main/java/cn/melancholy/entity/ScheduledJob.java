package cn.melancholy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@TableName("scheduled_job")
public class ScheduledJob {

    @TableId(value = "job_id",type = IdType.AUTO)
    private Integer jobId;

    private String jobKey;
    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务描述
     */
    private String taskExplain;
    /**
     * 推送卫视Pid
     */
    private String pushWx;
    /**
     * 推送内容
     */
    private String pushContext;
    /**
     * 推送模板
     */
    private String pushTemplate;
    /**
     * 任务启用状态
     */
    private Byte status;
}