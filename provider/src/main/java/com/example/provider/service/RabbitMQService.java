package com.example.provider.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.Map;

/**
 * @author wangli
 * @Description
 * @date 2022/11/9 15:34
 */
public interface RabbitMQService {
    /**
     * 发消息
     * @param msg
     * @return
     * @throws Exception
     */
    String sendMsg(String msg) throws Exception;

    String sendMsgByFanoutExchange(String msg) throws Exception;

    String sendMsgByTopicExchange(String msg, String routingKey) throws Exception;

    String sendMsgByHeadersExchange(String msg, Map<String, Object> map) throws Exception;


    void confirm(CorrelationData correlationData, boolean ack, String cause);
    void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey);
    String sendTTLMessage(String msg);
    String sendDelayedMessage(String msg, Integer delayTime);
}
