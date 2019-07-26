package cn.lastgo.sqs.service;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SQSQueueListener {

    @SqsListener(value = "demo-queue",deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void StringMessageListener(String message){

    }

    @SqsListener("test-transfer-fee-request")
    public void ProtoMessageListener(byte[] message){

    }

}
