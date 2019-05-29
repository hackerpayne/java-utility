
### 核心说明
listener 监听器
consumer 消费者。

### 第三方参考：
https://github.com/littlersmall/rabbitmq-access

### 批量消费说明  
如果消息堆积严重，我们可以通过两种方式来处理消息，一种是在服务端开启监听多线程服务（concurrency="10"），另一种是让消息批量出队列。

1、多线程消费  
2、批量出队列  
https://hbxflihua.iteye.com/blog/2415964  

QueueingConsumer 在客户端本地使用 BlockingQueue 缓冲消息，其nextDelivery方法也可以用于实现拉模式（其本质上是 BlockingQueue.take ），但是 QueueingConsumer 现在已经标记为Deprecated。

### Reactor-Rabbitmq
https://github.com/reactor/reactor-rabbitmq  
https://projectreactor.io/docs/rabbitmq/milestone/reference/#_getting_started 介绍文档  


