package com.yltrcc.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.yltrcc.demo.utils.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Package: PACKAGE_NAME
 * Date：2022-04-06
 * Time：16:53
 * Description：RabbitMQ 三种发布确认模式
 * 1.单个确认
 * 2. 批量确认
 * 3. 异步批量确认
 *
 * @author yltrcc
 * @version 1.0
 */
public class ConfirmMessage {

    public static void main(String[] args) throws Exception {
        //1.单个确认
        publishSingleMessage();
        //2. 批量确认
        publishBatchMessage();
        //3. 异步批量确认
        publishAsyncMessage();
    }

    /**
     * 单个确认
     */
    public static void publishSingleMessage() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //生命队列
        String queueName = UUID.randomUUID().toString();

        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息马上进行确认
            boolean b = channel.waitForConfirms();
            if (b) {
                System.out.println("消息发送成功！！！");
            }
        }

        //结束时间
        long end = System.currentTimeMillis();

        System.out.println("发送消息1000，单个发布确认用时： " + (end - begin) + " ms");
    }

    /**
     * 批量确认
     */
    public static void publishBatchMessage() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //生命队列
        String queueName = UUID.randomUUID().toString();

        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        int outstandingMessageCount = 0;

        //开始时间
        long begin = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            outstandingMessageCount++;
            if (outstandingMessageCount == batchSize) {
                channel.waitForConfirms();
                outstandingMessageCount = 0;
            }
        }
        //为了确保还有剩余没有确认消息 再次确认
        if (outstandingMessageCount > 0) {
            channel.waitForConfirms();
        }
        //结束时间
        long end = System.currentTimeMillis();

        System.out.println("发送消息1000，批量发布确认100个用时： " + (end - begin) + " ms");
    }

    /**
     * 异步批量确认
     *
     * @throws Exception
     */
    public static void publishAsyncMessage() throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();

            //线程安全有序的一个哈希表，适用于高并发的情况
            //1.轻松的将序号与消息进行关联 2.轻松批量删除条目 只要给到序列号 3.支持并发访问
            ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

            //确认收到消息的一个回调
            //1.消息序列号
            //2.multiple  是否是批量确认
            //false 确认当前序列号消息
            ConfirmCallback ackCallback = (sequenceNumber, multiple) -> {
                if (multiple) {
                    //返回的是小于等于当前序列号的未确认消息 是一个 map
                    ConcurrentNavigableMap<Long, String> confirmed =
                            outstandingConfirms.headMap(sequenceNumber, true);
                    //清除该部分未确认消息
                    confirmed.clear();
                } else {
                    //只清除当前序列号的消息
                    outstandingConfirms.remove(sequenceNumber);
                }
            };
            ConfirmCallback nackCallback = (sequenceNumber, multiple) -> {
                String message = outstandingConfirms.get(sequenceNumber);
                System.out.println("发布的消息" + message + "未被确认，序列号" + sequenceNumber);
            };

            //添加一个异步确认的监听器
            //1.确认收到消息的回调
            //2.未收到消息的回调
            channel.addConfirmListener(ackCallback, nackCallback);

            long begin = System.currentTimeMillis();

            for (int i = 0; i < 1000; i++) {
                String message = "消息" + i;
                //channel.getNextPublishSeqNo()获取下一个消息的序列号
                //通过序列号与消息体进行一个关联
                //全部都是未确认的消息体
                outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish("", queueName, null, message.getBytes());
            }
            long end = System.currentTimeMillis();
            System.out.println("发布" + 1000 + "个异步确认消息,耗时" + (end - begin) + "ms");
        }

    }
}
