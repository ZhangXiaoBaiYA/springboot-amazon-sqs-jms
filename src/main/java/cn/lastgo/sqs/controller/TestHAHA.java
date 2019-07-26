package cn.lastgo.sqs.controller;

import cn.lastgo.sqs.service.SQSQueue;
import cn.lastgo.sqs.vo.DemoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/")
public class TestHAHA {

    @Autowired
    SQSQueue sqsQueue;

    @GetMapping("/send")
    public void send(){
        //发送信息
        sqsQueue.send("test message sns");
    }

    @GetMapping("/send1")
    public void send1(){
        //发送object
        sqsQueue.send(new DemoMessage("http://localhost:8092/test","test for DemoMessage",new Date()));
    }

}
