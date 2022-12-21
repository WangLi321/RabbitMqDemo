package com.example.provider.config;

/**
 * @author wangli
 * @Description
 * @date 2022/11/9 15:14
 */
public class RabbitMQConfigConst {
    /**
     * RabbitMQ的队列主题名称
     */
    public static final String DIRECT_EXCHANGE_QUEUE_NAME = "direct.queue";

    /**
     * RabbitMQ的DIRECT交换机名称
     */
    public static final String DIRECT_EXCHANGE_NAME = "direct.exchange";

    /**
     * RabbitMQ的DIRECT交换机和队列绑定的匹配键 DirectRouting
     */
    public static final String DIRECT_EXCHANGE_ROUTING = "direct.routing";

    /**
     * RabbitMQ的FANOUT_EXCHANG交换机类型的队列 A 的名称
     */
    public static final String FANOUT_EXCHANGE_QUEUE_NAME_A = "fanout.queue.A";

    /**
     * RabbitMQ的FANOUT_EXCHANG交换机类型的队列 B 的名称
     */
    public static final String FANOUT_EXCHANGE_QUEUE_NAME_B = "fanout.queue.B";

    /**
     * RabbitMQ的FANOUT_EXCHANG交换机类型的名称
     */
    public static final String FANOUT_EXCHANGE_NAME = "fanout.exchange";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机名称
     */
    public static final String TOPIC_EXCHANGE_NAME = "topic.exchange";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机的队列A的名称
     */
    public static final String TOPIC_EXCHANGE_QUEUE_A = "topic.queue.a";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机的队列B的名称
     */
    public static final String TOPIC_EXCHANGE_QUEUE_B = "topic.queue.b";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机的队列C的名称
     */
    public static final String TOPIC_EXCHANGE_QUEUE_C = "topic.queue.c";

    /**
     * HEADERS_EXCHANGE交换机名称
     */
    public static final String HEADERS_EXCHANGE_NAME = "headers.exchange";

    /**
     * RabbitMQ的HEADERS_EXCHANGE交换机的队列A的名称
     */
    public static final String HEADERS_EXCHANGE_QUEUE_A = "headers.queue.a";

    /**
     * RabbitMQ的HEADERS_EXCHANGE交换机的队列B的名称
     */
    public static final String HEADERS_EXCHANGE_QUEUE_B = "headers.queue.b";

    //******************** TTL  *************************
    /**
     * TTL_direct交换机名称
     */
    public static final String TTL_DIRECT_EXCHANGE_NAME = "ttl.direct.exchange";

    /**
     * ttl_direct路由Key
     */
    public static final String TTL_DIRECT_EXCHANGE_ROUTING = "ttl.direct.routing";

    /**
     * ttl_direct队列名称
     */
    public static final String TTL_DIRECT_EXCHANGE_QUEUE = "ttl.direct.queue";


    // ***************** DLX ***************************

    /**
     * DLX_direct交换机名称
     */
    public static final String DLX_DIRECT_EXCHANGE = "dlx.direct.exchange";

    /**
     * dlx_direct队列名称
     */
    public static final String DLX_DIRECT_QUEUE = "dlx.direct.queue";

    /**
     * dlx_direct路由Key
     */
    public static final String DLX_DIRECT_ROUTING = "dlx.direct.routing";


    // ************  延迟消息 **********************
    /**
     * delayedDirect交换机名称
     */
    public static final String DELAYED_DIRECT_EXCHANGE = "delayed.direct.exchange";

    /**
     * delayed direct队列名称
     */
    public static final String DELAYED_DIRECT_QUEUE = "delayed.direct.queue";

    /**
     * delayed_direct路由Key
     */
    public static final String DELAYED_DIRECT_ROUTING = "delayed.direct.routing";

}
