package com.example.provider.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangli
 * @Description
 * @date 2022/11/11 14:13
 */
@Configuration
@Component
@Slf4j
public class RabbitMQConfig {

    /**
     * ************************************* DIRECT EXCHANGE *********************************
     */
    @Bean
    public Queue directExchangeQueue() {
        /**
         * 1、name:    队列名称
         * 2、durable: 是否持久化
         * 3、exclusive: 是否独享、排外的。如果设置为true，定义为排他队列。则只有创建者可以使用此队列。也就是private私有的。
         * 4、autoDelete: 是否自动删除。也就是临时队列。当最后一个消费者断开连接后，会自动删除。
         * */
        return new Queue(RabbitMQConfigConst.DIRECT_EXCHANGE_QUEUE_NAME, true, false, false);
    }

    @Bean
    public DirectExchange directExchange() {
        //Direct交换机
        return new DirectExchange(RabbitMQConfigConst.DIRECT_EXCHANGE_NAME, true, false);
    }

    /**
     * 定义一个队列和交换机的绑定
     *
     * @return
     */
    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directExchangeQueue()).to(directExchange()).with(RabbitMQConfigConst.DIRECT_EXCHANGE_ROUTING);
    }

    /**
     * ************************************* FANOUT EXCHANGE *********************************
     */
    @Bean
    public Queue fanoutExchangeQueueA() {
        //队列A
        return new Queue(RabbitMQConfigConst.FANOUT_EXCHANGE_QUEUE_NAME_A, true, false, false);
    }

    @Bean
    public Queue fanoutExchangeQueueB() {
        //队列B
        return new Queue(RabbitMQConfigConst.FANOUT_EXCHANGE_QUEUE_NAME_B, true, false, false);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        //创建FanoutExchange类型交换机
        return new FanoutExchange(RabbitMQConfigConst.FANOUT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindFanoutA() {
        //队列A绑定到FanoutExchange交换机
        return BindingBuilder.bind(fanoutExchangeQueueA()).to(fanoutExchange());
    }

    @Bean
    public Binding bindFanoutB() {
        //队列B绑定到FanoutExchange交换机
        return BindingBuilder.bind(fanoutExchangeQueueB()).to(fanoutExchange());
    }

    /**
     * ************************************* TOPIC EXCHANGE *********************************
     */
    @Bean
    public TopicExchange topicExchange() {
        //配置TopicExchange交换机
        return new TopicExchange(RabbitMQConfigConst.TOPIC_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue topicExchangeQueueA() {
        //创建队列1
        return new Queue(RabbitMQConfigConst.TOPIC_EXCHANGE_QUEUE_A, true, false, false);
    }

    @Bean
    public Queue topicExchangeQueueB() {
        //创建队列2
        return new Queue(RabbitMQConfigConst.TOPIC_EXCHANGE_QUEUE_B, true, false, false);
    }

    @Bean
    public Queue topicExchangeQueueC() {
        //创建队列3
        return new Queue(RabbitMQConfigConst.TOPIC_EXCHANGE_QUEUE_C, true, false, false);
    }

    @Bean
    public Binding bindTopicA() {
        //队列B绑定到TopicExchange交换机
        return BindingBuilder.bind(topicExchangeQueueB())
                .to(topicExchange())
                .with("a.*");
    }

    @Bean
    public Binding bindTopicB() {
        //队列C绑定到TopicExchange交换机
        return BindingBuilder.bind(topicExchangeQueueC())
                .to(topicExchange())
                .with("c.*");
    }

    @Bean
    public Binding bindTopicC() {
        //队列A绑定到TopicExchange交换机
        return BindingBuilder.bind(topicExchangeQueueA())
                .to(topicExchange())
                .with("rabbit.#");
    }

    /**
     * ************************************* HEADERS EXCHANGE *********************************
     */
    @Bean
    public Queue headersQueueA() {
        return new Queue(RabbitMQConfigConst.HEADERS_EXCHANGE_QUEUE_A, true, false, false);
    }

    @Bean
    public Queue headersQueueB() {
        return new Queue(RabbitMQConfigConst.HEADERS_EXCHANGE_QUEUE_B, true, false, false);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(RabbitMQConfigConst.HEADERS_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindHeadersA() {
        Map<String, Object> map = new HashMap<>();
        map.put("queue_a1", "java");
        map.put("queue_a2", "rabbit");
        //全匹配
        return BindingBuilder.bind(headersQueueA())
                .to(headersExchange())
                .whereAll(map).match();
    }

    @Bean
    public Binding bindHeadersB() {
        Map<String, Object> map = new HashMap<>();
        map.put("queue_b1", "coke");
        map.put("queue_b2", "sky");
        //部分匹配
        return BindingBuilder.bind(headersQueueB())
                .to(headersExchange())
                .whereAny(map).match();
    }


    /** ************************************** TTL EXCHANGE ********************************* */
    /**
     * /** 定义一个TTL direct交换机
     *
     * @return
     */
    @Bean
    public DirectExchange ttlDirectExchange() {
        return new DirectExchange(RabbitMQConfigConst.TTL_DIRECT_EXCHANGE_NAME);
    }

    /**
     * 定义一个TTL direct队列
     *
     * @return
     */
    @Bean
    public Queue ttlDirectExchangeQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-message-ttl", 50000);
        map.put("x-dead-letter-exchange", RabbitMQConfigConst.DLX_DIRECT_EXCHANGE);
        map.put("x-dead-letter-routing-key", RabbitMQConfigConst.DLX_DIRECT_ROUTING);
        map.put("x-max-length", 100);   // 队列中最多消息数量限制
        return new Queue(RabbitMQConfigConst.TTL_DIRECT_EXCHANGE_QUEUE, true, false, false, map);
    }

    /**
     * TTL定义一个队列和交换机的绑定
     *
     * @return
     */
    @Bean
    public Binding ttlDirectBinding() {
        return BindingBuilder.bind(ttlDirectExchangeQueue()).to(ttlDirectExchange()).with(RabbitMQConfigConst.TTL_DIRECT_EXCHANGE_ROUTING);
    }

    /** ************************************** DLX EXCHANGE死信队列 ********************************* */
    /**
     * /**
     * 定义一个DLX direct交换机
     *
     * @return
     */
    @Bean
    public DirectExchange dlxDirectExchange() {
        return new DirectExchange(RabbitMQConfigConst.DLX_DIRECT_EXCHANGE);
    }

    /**
     * 定义一个DLX direct队列
     *
     * @return
     */
    @Bean
    public Queue dlxDirectQueue() {
        return new Queue(RabbitMQConfigConst.DLX_DIRECT_QUEUE);
    }

    /**
     * dlx定义一个队列和交换机的绑定
     *
     * @return
     */
    @Bean
    public Binding dlxDirectBinding() {
        return BindingBuilder.bind(dlxDirectQueue()).to(dlxDirectExchange()).with(RabbitMQConfigConst.DLX_DIRECT_ROUTING);
    }


    /** ************************************** 延迟消息 ********************************* */

    /**
     * 定义一个delayed direct交换机
     *
     * @return
     */
    @Bean
    public CustomExchange delayedDirectExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(RabbitMQConfigConst.DELAYED_DIRECT_EXCHANGE, "x-delayed-message", true, false, args);

    }

    /**
     * 定义一个DELAYED direct队列
     *
     * @return
     */
    @Bean
    public Queue delayedDirectQueue() {
        return new Queue(RabbitMQConfigConst.DELAYED_DIRECT_QUEUE);
    }

    /**
     * 定义一个队列和交换机的绑定
     *
     * @return
     */
    @Bean
    public Binding delayedDirectBinding() {
        return BindingBuilder.bind(delayedDirectQueue()).to(delayedDirectExchange()).with(RabbitMQConfigConst.DELAYED_DIRECT_ROUTING).noargs();
    }

}
