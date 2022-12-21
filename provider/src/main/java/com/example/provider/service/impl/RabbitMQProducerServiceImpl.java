package com.example.provider.service.impl;

import com.example.provider.config.RabbitMQConfigConst;
import com.example.provider.service.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author wangli
 * @Description
 * @date 2022/11/9 15:34
 */
@Service
@Slf4j
public class RabbitMQProducerServiceImpl implements RabbitMQService, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    //日期格式化
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 当然，init方法在spring自动注入rabbitTemplate之后执行
     */
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public String sendMsg(String msg) throws Exception {
        try {
            //发送的时候携带的数据，唯一标识
            String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            CorrelationData correlationData = new CorrelationData(msgId);
            rabbitTemplate.convertAndSend(RabbitMQConfigConst.DIRECT_EXCHANGE_NAME, RabbitMQConfigConst.DIRECT_EXCHANGE_ROUTING, getMessage(msg), correlationData);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    //发布消息
    @Override
    public String sendMsgByFanoutExchange(String msg) throws Exception {
        Map<String, Object> message = getMessage(msg);
        try {
            String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            CorrelationData correlationData = new CorrelationData(msgId);
            rabbitTemplate.convertAndSend(RabbitMQConfigConst.FANOUT_EXCHANGE_NAME, "", message, correlationData);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    public String sendMsgByTopicExchange(String msg, String routingKey) throws Exception {
        Map<String, Object> message = getMessage(msg);
        try {
            String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            CorrelationData correlationData = new CorrelationData(msgId);
            //发送消息
            rabbitTemplate.convertAndSend(RabbitMQConfigConst.TOPIC_EXCHANGE_NAME, routingKey, message, correlationData);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    @Override
    public String sendMsgByHeadersExchange(String msg, Map<String, Object> map) throws Exception {
        try {
            MessageProperties messageProperties = new MessageProperties();
            //消息持久化
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            messageProperties.setContentType("UTF-8");
            //添加消息
            messageProperties.getHeaders().putAll(map);
            Message message = new Message(msg.getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(RabbitMQConfigConst.HEADERS_EXCHANGE_NAME, null, message);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    //组装消息体
    private Map<String, Object> getMessage(String msg) {
        String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        String sendTime = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", msgId);
        map.put("sendTime", sendTime);
        map.put("msg", msg);
        return map;
    }

    /**
     * @param correlationData 消息唯一标识, 存在的意义便是：如果消息发送失败，可以根据这个标识补发消息
     * @param ack             交换机是否成功收到消息 true成功  false失败
     * @param cause           失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("confirm方法被执行了..." + correlationData);
        if (ack) {
            log.info("交换机 ，消息接收成功");
        } else {
            log.info("交换机 ，消息接收失败" + cause);
            // 这里可以接收一些消息补发的措施
            log.info("id=" + correlationData.getId());
        }

    }

    /**
     * @param message    消息主体
     * @param replyCode  返回code
     * @param replyText  返回信息
     * @param exchange   交换机
     * @param routingKey 路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("return方法被执行...");
        log.info("消息主体：" + new String(message.getBody())); // 得到消息对象，通过id再进行消息的补发
        log.info("replyCode：" + replyCode);
        log.info("replyText：" + replyText);
        log.info("exchange：" + exchange);
        log.info("routingKey：" + routingKey);
    }

    @Override
    public String sendTTLMessage(String msg) {
        String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        CorrelationData correlationData = new CorrelationData(msgId);
        rabbitTemplate.convertAndSend(RabbitMQConfigConst.TTL_DIRECT_EXCHANGE_NAME, RabbitMQConfigConst.TTL_DIRECT_EXCHANGE_ROUTING, getMessage(msg), correlationData);
        return "ok";
    }

    @Override
    public String sendDelayedMessage(String msg, Integer delayTime) {
        log.info("发送延迟消息：{},延迟时间：{},当前时间：{}", msg, delayTime, new Date().toString());
        amqpTemplate.convertAndSend(RabbitMQConfigConst.DELAYED_DIRECT_EXCHANGE, RabbitMQConfigConst.DELAYED_DIRECT_ROUTING, getMessage(msg), a -> {
            a.getMessageProperties().setDelay(delayTime);
            return a;
        });
        return "ok";
    }
}
