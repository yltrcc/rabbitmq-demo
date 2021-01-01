
# 第一个案例

使用最原始的写法：
1. 创建连接工厂
```java
//创建一个连接工厂
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("127.0.0.1");
factory.setUsername("guest");
factory.setPassword("guest");
```

2. 建立连接
```java
//创建连接
Connection connection = factory.newConnection();
```

3. 建立信道
```java
 //创建信道
Channel channel = connection.createChannel();
```

4. 发送或者消费消息
```java

//创建一个连接工厂
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("127.0.0.1");
factory.setUsername("guest");
factory.setPassword("guest");
//channel 实现了自动 close 接口 自动关闭 不需要显示关闭
try (Connection connection = factory.newConnection();Channel channel = connection.createChannel()){
    // 生成一个队列
    // 1.队列名称
    // 2.队列里面的消息是否持久化 默认消息存储在内存中
    // 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
    // 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
    // 5.其他参数
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    String message = "hello world";
    //发送一个消息
    //1.发送到那个交换机
    //2.路由的 key 是哪个
    //3.其他的参数信息
    //4.发送消息的消息体
    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
    System.out.println("消息发送完毕");
}
```

```java
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
```
    
缺点：简单队列是一一对应的关系，即点对点，一个生产者对应一个消费者