package com.yltrcc.demo;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.yltrcc.demo.utils.RabbitMqUtils;

/**
 * Package: com.yltrcc.demo
 * Date：2022-01-22
 * Time：23:03
 * Description：这是一个工作线程（相当于Demo1中的消费者 Consumer 慢消费者）
 *
 * @author yltrcc
 * @version 1.0
 */
public class Worker02 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                Thread.sleep(1000);
                String receivedMessage = new String(delivery.getBody());
                System.out.println("接收到消息:" + receivedMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };
        //手动确认
        boolean autoAck = false;
        System.out.println("C2 消费者启动等待消费.................. ");
        //消费者消费消息
        //1. 消费哪个队列
        //2. 消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
        //3. 消费者未成功消费的回调
        //4. 消费者取消消费的回调
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);

    }

}
