package cn.lastgo.sqs.service;

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SQSQueueListener {

    @SqsListener("demo-queue")
    public void queueListener(String message){
        System.out.println(message);
    }



}
