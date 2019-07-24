package cn.lastgo.sqs.service;

import cn.lastgo.sqs.vo.DemoMessage;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
public class SQSQueueListener {

    @SqsListener("demo-queue")
    public void queueListener(DemoMessage message){
        System.out.println(message.url);
        System.out.println(message.content);
        System.out.println(message.date);
    }

    @GetMapping("test-queue")
    public void StringMessageListener(String message){
        System.out.println(message);
    }

}
