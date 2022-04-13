package com.yltrcc.demo;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.yltrcc.demo.utils.RabbitMqUtils;

/**
 * Package: com.yltrcc.demo
 * Date：2022-01-22
 * Time：23:03
 * Description：这是一个工作线程（相当于Demo1中的消费者 Consumer）
 *
 * @author yltrcc
 * @version 1.0
 */
public class Worker02 {

    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                //沉睡 10 s
                Thread.sleep(1000 * 30);
                String receivedMessage = new String(delivery.getBody());
                System.out.println("接收到消息:" + receivedMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(" [x] Done");
                //* 1. 消息的标记 tag
                //* 2. 是否批量应答，false 不批量应答信道中的消息
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };
        System.out.println("C2 消费者启动等待消费,处理时间较长.................. ");
        //手动确认
        boolean autoAck = false;
        //消费者消费消息
        //1. 消费哪个队列
        //2. 消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
        //3. 消费者消费的回调
        //4. 消费者取消消费的回调
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);

    }

}
