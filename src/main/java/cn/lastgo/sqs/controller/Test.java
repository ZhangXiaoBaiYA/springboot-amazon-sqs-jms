package cn.lastgo.sqs.controller;

import cn.lastgo.sqs.service.SQSQueueSender;
import cn.lastgo.sqs.vo.DemoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/")
public class Test {

    @Autowired
    SQSQueueSender sqsQueueSender;

    @GetMapping("/send")
    public void send(){
        //发送信息
        sqsQueueSender.send("test message sns");
    }

    @GetMapping("/send1")
    public void send1(){
        //发送object
        sqsQueueSender.send(new DemoMessage("http://localhost:8092/test","test for DemoMessage",new Date()));
    }
}
