package cn.lastgo.sqs.service;

import cn.lastgo.sqs.vo.DemoMessage;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SQSQueue {
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public SQSQueue(AmazonSQSAsync amazonSqs) {
        this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
    }

    public void send(String message) {
        this.queueMessagingTemplate.send("demo-queue", MessageBuilder.withPayload(message).build());
    }

    public void send(DemoMessage message) {
        this.queueMessagingTemplate.convertAndSend("demo-queue",message);
    }

}
