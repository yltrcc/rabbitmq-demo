package com.yltrcc.demo;

import com.rabbitmq.client.Channel;
import com.yltrcc.demo.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * Package: com.yltrcc.demo
 * Date：2022-01-22
 * Time：23:10
 * Description：生产者 - 发送大量消息
 *
 * @author yltrcc
 * @version 1.0
 */
public class Task01 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel();) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //循环发送消息
            for (int i = 1; i <= 50; i++) {
                String msg = "生产者消息_" + i;
                System.out.println("生产者发送消息:" + msg);
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            }
        }
    }

}
