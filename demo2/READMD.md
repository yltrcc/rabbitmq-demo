# 第二个例子

工作队列是公平分发的，轮询的效果平均分配任务。

假如有一些非常耗时的任务，某个消费者在缓慢地进行处理，而另一个消费者则空闲，显然是非常消耗资源的。