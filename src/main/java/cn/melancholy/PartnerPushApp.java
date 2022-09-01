package cn.melancholy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"cn.melancholy.mapper"})
public class PartnerPushApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(PartnerPushApp.class,args);
    }
}
