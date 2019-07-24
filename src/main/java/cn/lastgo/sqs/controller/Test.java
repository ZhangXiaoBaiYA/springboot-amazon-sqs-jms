package cn.lastgo.sqs.controller;

import cn.lastgo.sqs.service.SQSQueueSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
