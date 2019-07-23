package cn.lastgo.sqs.producer;

import cn.lastgo.sqs.vo.DemoMessage;
import cn.lastgo.sqs.vo.MyMessage;
import com.amazon.sqs.javamessaging.SQSMessagingClientConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 消息生产者
 */

@Component
public class Producer {


    private final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    @Autowired
    protected JmsTemplate jmsTemplate;

    @Value("${queue.stand}")
    String standQueue;

    @Value("${queue.fifo}")
    String fifoQueue;

    @Value("${queue.test}")
    String testQueue;

    @Autowired
    ObjectMapper objectMapper;


    public void sendToStandQueue(MyMessage message) {
        LOGGER.info("Sending message {} to queue {}", message, standQueue);
        send(standQueue, message);
    }

    public void sendToFifoQueue(String message) {
        LOGGER.info("Sending message {} to queue {}", message, fifoQueue);
        send(fifoQueue, message);
    }

    public void sendToTestQueue(DemoMessage message){
        LOGGER.info("Sending message {} to queue {}", message, fifoQueue);
        send(testQueue, message);
    }

    /**
     * 发送信息
     * @param queue
     * @param payload
     * @param <MESSAGE>
     */
    public <MESSAGE extends Serializable> void send(String queue, MESSAGE payload) {
        jmsTemplate.send(queue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                try {
                    Message createMessage = session.createTextMessage(objectMapper.writeValueAsString(payload));
                    createMessage.setStringProperty(SQSMessagingClientConstants.JMSX_GROUP_ID, "messageGroup1");
                    createMessage.setStringProperty(SQSMessagingClientConstants.JMS_SQS_DEDUPLICATION_ID, "2019" + System.currentTimeMillis());
                    createMessage.setStringProperty("documentType", payload.getClass().getName());
                    return createMessage;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    LOGGER.error("Fail to send message {} ,err {}", payload, e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //发起请求
    @PostConstruct
    public void sendMessage(){

        MyMessage standMessage = new MyMessage(UUID.randomUUID().toString(),"Hello Stand queue!",new Date());

        sendToStandQueue(standMessage);

        sendToFifoQueue("Hello Fifo queue!");

        DemoMessage demoMessage = new DemoMessage(UUID.randomUUID().toString(),"Test queue",new Date());

        sendToTestQueue(demoMessage);
    }

}
