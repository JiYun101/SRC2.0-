package com.msb.crm.task;

import com.msb.crm.service.CustomerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JobTask {
    @Resource
    private CustomerService customerService;

    //@Scheduled(cron = "0/60 * * * * *")
    public void job(){
        System.out.println("定时任务开始执行--》"+new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date()));
        customerService.updateCustomerState();
    }
}
