package com.yltrcc.demo.utils;


import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * Package: com.yltrcc.demo.utils
 * Date：2022-04-13
 * Time：17:06
 * Description：消息接收
 *
 * @author yltrcc
 * @version 1.0
 */
public class ReceiveLogs02 {

    //交换机的名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        //声明一个临时队列
        //队列名称是随机的，当消费者断开与队列连接时，队列就自动删除
        String queueName = channel.queueDeclare().getQueue();

        //绑定交换机与队列
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息，吧接收到的消息打印在屏幕上....");

        //声明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogs02控制台打印接收到的消息：" + new String(message.getBody()));
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
