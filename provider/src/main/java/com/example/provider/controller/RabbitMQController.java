package com.example.provider.controller;

import com.example.provider.service.RabbitMQService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author wangli
 * @Description
 * @date 2022/11/9 15:43
 */
@RestController
@RequestMapping("/wl/rabbitmq")
@Slf4j
public class RabbitMQController {
    @Resource
    private RabbitMQService rabbitMQService;
    /**
     * 发送消息
     */
    @PostMapping("/directSend")
    public String sendMsg(@RequestParam(name = "msg") String msg) throws Exception {
        return rabbitMQService.sendMsg(msg);
    }

    @PostMapping("/fanoutSend")
    public String publish(@RequestParam(name = "msg") String msg) throws Exception {
        return rabbitMQService.sendMsgByFanoutExchange(msg);
    }

    @PostMapping("/topicSend")
    public String topicSend(@RequestParam(name = "msg") String msg, @RequestParam(name = "routingKey") String routingKey) throws Exception {
        return rabbitMQService.sendMsgByTopicExchange(msg, routingKey);
    }

    @PostMapping("/headersSend")
    @SuppressWarnings("unchecked")
    public String headersSend(@RequestParam(name = "msg") String msg,
                              @RequestParam(name = "json") String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class);
        return rabbitMQService.sendMsgByHeadersExchange(msg, map);
    }

    @PostMapping("/delayedSend")
    @SuppressWarnings("unchecked")
    public String delayedSend(@RequestParam(name = "msg") String msg) throws Exception {
        return rabbitMQService.sendDelayedMessage(msg, 50000);
    }

    @PostMapping("/TTLSend")
    @SuppressWarnings("unchecked")
    public String TTLSend(@RequestParam(name = "msg") String msg) throws Exception {
        return rabbitMQService.sendTTLMessage(msg);
    }
}
