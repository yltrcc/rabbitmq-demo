package com.yltrcc.demo;

import com.rabbitmq.client.*;

/**
 * Package: com.yltrcc.demo
 * Date：2022-01-22
 * Time：22:39
 * Description：消费消息
 *
 * @author yltrcc
 * @version 1.0
 */
public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        System.out.println("等待接收消息.........");

        //声明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody()));
        };

        //取消消息回调
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消息消费被中断");
        };

        //消费者消费消息
        //1. 消费哪个队列
        //2. 消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
        //3. 消费者未成功消费的回调
        //4. 消费者取消消费的回调
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
