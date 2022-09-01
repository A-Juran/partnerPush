package cn.melancholy;

import cn.melancholy.config.WxConfigure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@SpringBootTest(classes = PartnerPushApp.class)
@RunWith(SpringRunner.class)
public class AppTest 
{
    @Autowired
    private WxConfigure wxConfigure;
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        System.out.println(wxConfigure.getApp_id());
    }
}
