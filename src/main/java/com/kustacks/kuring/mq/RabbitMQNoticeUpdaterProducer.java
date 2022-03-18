package com.kustacks.kuring.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.mq.dto.NoticeUpdateRequestMQMessageDTO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class RabbitMQNoticeUpdaterProducer implements MQUpdaterProducer<NoticeUpdateRequestMQMessageDTO> {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.virtual-host}")
    private String vhost;

    @Value("${spring.rabbitmq.password}")
    private String password;

    private final ObjectMapper objectMapper;

    private Channel channel;

    private final static String EXCHANGE_NAME = "amq.direct";
    private final static String ROUTING_KEY = "updater.notice";
    private final static String QUEUE_NAME = "updater.notice";

    public RabbitMQNoticeUpdaterProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(NoticeUpdateRequestMQMessageDTO messageDTO) {
        if(channel == null) {
            initChannel();
        }

        try {
            String body = objectMapper.writeValueAsString(messageDTO);
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, null, body.getBytes(StandardCharsets.UTF_8));
        } catch(JsonProcessingException e) {
            throw new InternalLogicException(ErrorCode.MQ_CANNOT_CONVERT_OBJECT_TO_STRING);
        } catch(IOException e) {
            throw new InternalLogicException(ErrorCode.MQ_CANNOT_PUBLISH);
        }
    }

    @Override
    public void publish(List<NoticeUpdateRequestMQMessageDTO> messageDTOList) {
        for (NoticeUpdateRequestMQMessageDTO messageDTO : messageDTOList) {
            publish(messageDTO);
        }
    }

    private void initChannel() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(vhost);
        factory.setHost(host);
        factory.setPort(port);
        factory.setConnectionTimeout(3000);

        try {
            Connection connection = factory.newConnection();
            this.channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        } catch (IOException | TimeoutException e) {
            throw new InternalLogicException(ErrorCode.MQ_CANNOT_CONNECT, e);
        }

        // 메세지를 basicPublish로 보낼 때, mandatory를 true로 주면
        // RabbitMQ가 모종의 이유로 메세지를 적당한 큐로 라우팅하지 못했을 때 producer에게 이 사실을 알린다.
        // 그 상황을 처리하기 위한 핸들러
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
            log.error("공지 업데이트 요청 메세지의 unroutable 오류");
            log.error("ReplyCode = {}, ReplyText = {}", replyCode, replyText);
            log.error("Exchange = {}, Routing Key = {}", exchange, routingKey);
            log.error(String.valueOf(body));
        });
    }
}
