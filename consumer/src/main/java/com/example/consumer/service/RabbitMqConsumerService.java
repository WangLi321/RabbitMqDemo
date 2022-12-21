package com.example.consumer.service;

import com.example.consumer.config.RabbitMQConfigConst;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class RabbitMqConsumerService {

    @RabbitListener(queues = {RabbitMQConfigConst.DIRECT_EXCHANGE_QUEUE_NAME})
    public void receiveDirectMessage(Map<String, Object> message, Channel channel) {
        log.info("Direct队列收到消息：" + message);
    }

    @RabbitListener(queues = {RabbitMQConfigConst.FANOUT_EXCHANGE_QUEUE_NAME_A})
    public void receiveFanoutAMessage(Map<String, Object> message, Channel channel) {
        log.info("队列fanout.queue.A收到消息：" + message);
    }

    @RabbitListener(queues = {RabbitMQConfigConst.FANOUT_EXCHANGE_QUEUE_NAME_B})
    public void receiveFanoutBMessage(Map<String, Object> message, Channel channel) {
        log.info("队列fanout.queue.B收到消息：" + message);

    }

    @RabbitListener(queues = {RabbitMQConfigConst.TOPIC_EXCHANGE_QUEUE_A})
    public void receiveTopicAMessage(Map<String, Object> message, Channel channel) {
        log.info("队列topic.queue.a收到消息：" + message);

    }

    @RabbitListener(queues = {RabbitMQConfigConst.TOPIC_EXCHANGE_QUEUE_B})
    public void receiveTopicBMessage(Map<String, Object> message, Channel channel) {
        log.info("队列topic.queue.b收到消息：" + message);

    }

    @RabbitListener(queues = {RabbitMQConfigConst.TOPIC_EXCHANGE_QUEUE_C})
    public void receiveTopicCMessage(Map<String, Object> message, Channel channel) {
        log.info("队列topic.queue.c收到消息：" + message);

    }

    @RabbitListener(queues = {RabbitMQConfigConst.HEADERS_EXCHANGE_QUEUE_A})
    public void receiveHeadersAMessage(String message, Channel channel) {
        log.info("队列headers.queue.a收到消息：" + message);
    }

    @RabbitListener(queues = {RabbitMQConfigConst.HEADERS_EXCHANGE_QUEUE_B})
    public void receiveHeadersBMessage(String message, Channel channel) {
        log.info("队列headers.queue.b收到消息：" + message);
    }

    /** ************************************************  签收  ********************************************** **/

    @RabbitListener(queues = {RabbitMQConfigConst.TTL_DIRECT_EXCHANGE_QUEUE})
    public void receiveTtlMessage(@Payload Map<String, Object> message, Channel channel, Message messageBody) throws Exception {

        long deliveryTag = messageBody.getMessageProperties().getDeliveryTag();
        try {
            //业务逻辑处理......
            log.info("receiveTtlMessage收到deliveryTag : " + deliveryTag + ", 收到消息: " + message);
            //消息确认，deliveryTag:该消息的index，multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息。
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            if (messageBody.getMessageProperties().getRedelivered()) {
                log.error("{}, 消息已重复处理失败,拒绝再次接收...", message);
                channel.basicReject(messageBody.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
            } else {
                log.error("消息即将再次返回队列处理...");
                channel.basicNack(messageBody.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
//
//    @RabbitListener(queues = {RabbitMQConfigConst.DIRECT_EXCHANGE_QUEUE_NAME})
//    public void getMessage(@Payload byte[] message, Channel channel, @Headers Map<String, Object> headers) throws Exception {
//
//        long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
//        //做拒绝查看demo，没有死信队列，所以拒绝后会一直重发，方便查看rabbimq消息
//        if(deliveryTag > 4) {
//            log.info("deliveryTag:" + deliveryTag + "，---------消息拒绝 : " + (new String(message)));
//            //消息拒绝deliveryTag:该消息的index，requeue：被拒绝的是否重新入队列(false:不进入，true:进入)
//            channel.basicReject(deliveryTag, true);
////            channel.basicNack(deliveryTag , false,true);
//        } else {
//            log.info("幂等处理 & 业务处理 getMessage消费者byte收到deliveryTag : " + deliveryTag + "消费者byte收到消息  : " + (new String(message)));
//            channel.basicAck(deliveryTag, false);
//        }
//    }
//
//
//    /**
//     * 通过channel的basicAck方法手动签收，通过basicNack方法拒绝签收
//     * 注： concurrency = "5-8"  与 containerFactory = "limitContainerFactory" 当然可以同时使用，但推荐还是使用一个
//     */
//    @RabbitListener(queues = {RabbitMQConfigConst.DIRECT_EXCHANGE_QUEUE_NAME})
//    public void receiveDirectMessage(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
//        try {
//            //String message= (String)amqpTemplate.receiveAndConvert(RabbitMQConfig.DIRECT_QUEUE);
//            log.info(System.currentTimeMillis() + "接收的mq direct.queue消息：" + message);
//
//            // 业务处理 异常测试
//            // log.info("业务处理"+1/0);
//
//            // long deliveryTag 消息接收tag boolean multiple 是否批量确认
//            log.info("deliveryTag=" + deliveryTag);
//
//            /**
//             * 无异常就确认消息
//             * basicAck(long deliveryTag, boolean multiple)
//             * deliveryTag:取出来当前消息在队列中的的索引;
//             * multiple:为true的话就是批量确认,如果当前deliveryTag为5,那么就会确认
//             * deliveryTag为5及其以下的消息;一般设置为false
//             */
//
////            if(deliveryTag==5){
////                channel.basicAck(deliveryTag,true);
////            }
//            channel.basicAck(deliveryTag, true);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//            /**
//             * 有异常就绝收消息
//             * basicNack(long deliveryTag, boolean multiple, boolean requeue)
//             * requeue:true为将消息重返当前消息队列,还可以重新发送给消费者;
//             *         false:将消息丢弃
//             */
//
//            // long deliveryTag, boolean multiple, boolean requeue
//
//            try {
//
//                channel.basicNack(deliveryTag, false, true);
//                // long deliveryTag, boolean requeue
//                // channel.basicReject(deliveryTag,true);
//
//                //Thread.sleep(1000);     // 这里只是便于出现死循环时查看
//
//                /*
//                 * 一般实际异常情况下的处理过程：记录出现异常的业务数据，将它单独插入到一个单独的模块，
//                 * 然后尝试3次，如果还是处理失败的话，就进行人工介入处理
//                 */
//
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//
//        }
//    }

    @RabbitListener(queues = {RabbitMQConfigConst.DELAYED_DIRECT_QUEUE})
    public void receiveDelayMessage(Map<String, Object> message) {
        log.info("接收延迟消息：" + message + ":" + new Date().toString());
    }

    @RabbitListener(queues = {RabbitMQConfigConst.DLX_DIRECT_QUEUE})
    public void receiveDlxMessage(Map<String, Object> message) {
        log.info("接收死信消息：" + message + ":" + new Date().toString());
    }
}
