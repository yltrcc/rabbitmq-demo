package com.yltrcc.demo.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Package: com.yltrcc.demo.utils
 * Date：2022-01-22
 * Time：23:01
 * Description：TODO
 *
 * @author yltrcc
 * @version 1.0
 */
public class RabbitMqUtils {

    /**
     *
     * @return 得到一个连接的 channel
     * @throws Exception 抛出连接异常
     */
    public static Channel getChannel() throws Exception {

        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

}
