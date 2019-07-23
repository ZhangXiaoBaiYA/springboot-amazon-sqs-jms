package cn.lastgo.sqs.consumer;

import cn.lastgo.sqs.vo.DemoMessage;
import cn.lastgo.sqs.vo.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    /**
     * 监听器，用于消费消息队列
     * 对消息队列中信息进行处理
     */

    private final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    @JmsListener(destination = "${queue.fifo}")
    public void listenQueueFifo(@Payload String message) {
        LOGGER.info("Listening {} in queue fifo", message);
    }


    @JmsListener(destination = "${queue.stand}")
    public void listenQueueStand(@Payload final Message<MyMessage> message) {
        LOGGER.info("Listening {} in queue stand", message.getPayload());
    }


    @JmsListener(destination = "${queue.test}")
    public void listenQueueTest(@Payload final Message<DemoMessage> message) {
        LOGGER.info("Listening {} in queue test", message.getPayload().content);
    }
}
