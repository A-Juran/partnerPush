package cn.melancholy.taskController;

import cn.melancholy.service.PartnerService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 作者： Juran on 2022-09-01 19:00
 * 作者博客：iit.la
 */
@Component
@EnableScheduling
public class PartnerPush implements Serializable {

    final
    PartnerService partnerService;

    public PartnerPush(PartnerService partnerService) {
        this.partnerService = partnerService;
    }
}
