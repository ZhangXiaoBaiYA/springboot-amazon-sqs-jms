# springboot-amazon-sqs-jms
使用springboot + amazon(SQS) + jms  消息队列的使用

## 前提
+ 首先要有amazon账户
+ [使用Application Integration  SQS服务，创建队列](https://console.aws.amazon.com/sqs/home?region=ap-northeast-1#create-queue:noRefresh=true;prefix=$)
+ [Amazon SQS Region](https://docs.aws.amazon.com/zh_cn/general/latest/gr/rande.html#sqs_region)


## 使用spring cloud   
    分支名称 spring+aws 更简单。。。

在pom.xml文件中引入以下依赖
```
        <!--引入 SQS依赖 -->
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>amazon-sqs-java-messaging-lib</artifactId>
            <version>1.0.4</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
        </dependency>

        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk</artifactId>
            <version>1.9.6</version>
        </dependency>

        <!--json 依赖-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <!--log 依赖-->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.3</version>
        </dependency>
```

### 配置文件 application.properties
```
# request key
cloud.aws.credentials.accessKey=请输入您的凭证KEY
cloud.aws.credentials.secretKey=请输入您的凭证密钥
cloud.aws.region.static=ap-northeast-1
cloud.aws.endpoint.static=sqs.ap-northeast-1.amazonaws.com

#队列名称  fifo队列必须以 .fifo结尾
queue.stand=demo-queue
queue.fifo=fifo-queue.fifo
queue.test=test-queue
```
### 编写Jms config
```

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.annotation.PostConstruct;
import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfig {

    public final static Logger LOGGER = LoggerFactory.getLogger(JmsConfig.class);

    //创建链接SQS服务对象
    private final AmazonSQSClientBuilder SQSClientBuilder;
    private final SQSConnectionFactory connectionFactory;

    @PostConstruct
    public void init() {
        LOGGER.info("Started init");
    }

    public JmsConfig(
            @Value("${cloud.aws.credentials.accessKey}") String awsAccessKey,
            @Value("${cloud.aws.credentials.secretKey}") String awsSecretKey,
            @Value("${cloud.aws.region.static}") String awsRegion,
            @Value("${cloud.aws.endpoint.static}") String endPoint) {
        SQSClientBuilder = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, awsRegion));
        connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),this.SQSClientBuilder);
    }


    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate(this.connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter());
        return jmsTemplate;
    }

    /**
     * 格式 Format
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.dateFormat(new ISO8601DateFormat());
        org.springframework.jms.support.converter.MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setObjectMapper(builder.build());
        mappingJackson2MessageConverter.setTargetType(MessageType.TEXT);
        mappingJackson2MessageConverter.setTypeIdPropertyName("documentType");
        return mappingJackson2MessageConverter;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        //设置连接工厂
        factory.setConnectionFactory(this.connectionFactory);
        //设置动态解决程序
        factory.setDestinationResolver(new DynamicDestinationResolver());

        //设置并发性
        //The values we provided to Concurrency show that we will create a minimum of 3 listeners that will scale up to 10 listeners
        factory.setConcurrency("3-10");

        //确认模式
        /**
         *  SESSION_TRANSACTED
         *      事务提交并确认
         *  CLIENT_ACKNOWLEDGE
         *      客户端确认完成后，客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法,确认后jms服务器才会删除信息
         *  AUTO_ACKNOWLEDGE
         *      自动确认，客户端发送和接收消息不需要做额外的工作
         *  DUPS_OK_ACKNOWLEDGE
         *      允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。在需要考虑资源使用时，这种模式非常有效。
         */
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        //信息转换
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
```

### 编写VO，用于接收和发送信息
```
import java.io.Serializable;
import java.util.Date;

public class DemoMessage implements Serializable {

    private static final long serialVersionUID = -8013965441896177936L;
    public String url;
    public String content;
    public Date date;

    public DemoMessage(String url, String content, Date date) {
        this.url = url;
        this.content = content;
        this.date = date;
    }

    public DemoMessage() {
    }
}
```

### 编写消息生产者
```
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
    
    @Value("${queue.test}")
    String testQueue;

    @Autowired
    ObjectMapper objectMapper;

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
        DemoMessage demoMessage = new DemoMessage(UUID.randomUUID().toString(),"Test queue",new Date());
        sendToTestQueue(demoMessage);
    }
}
```

### 消息消费者
```
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
    
    @JmsListener(destination = "${queue.test}")
    public void listenQueueTest(@Payload final Message<DemoMessage> message) {
        LOGGER.info("Listening {} in queue test", message.getPayload().content);
    }
}
```
