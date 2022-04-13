package com.yltrcc.demo;

import com.rabbitmq.client.Channel;
import com.yltrcc.demo.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * Package: com.yltrcc.demo
 * Date：2022-01-22
 * Time：23:10
 * Description：消息在手动应答时不丢失、返回队列中重新消费
 *
 * @author yltrcc
 * @version 1.0
 */
public class Task {

    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel();) {
            channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
            //循环发送消息 控制条
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String message = scanner.next();
                channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes());
                System.out.println("生产者发送消息:" + message);
            }
        }
    }

}
