package com.lingdonge.rabbit.producer;

import com.lingdonge.rabbit.service.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * reactor-rabbitmq对rabbitmq的api进行封装，改造为reactive streams模式，提供了Non-blocking Back-pressure以及End-to-end Reactive Pipeline特性。
 * http://projectreactor.io/docs/rabbitmq/milestone/reference/#_getting_started
 */
@Slf4j
public class ReactorRabbitMQUtil {

    private static String QUEUE = "test_queue";

    private ConnectionFactory connectionFactory;

    public ReactorRabbitMQUtil() throws IOException, TimeoutException {
        RabbitProperties rabbitProperties = new RabbitProperties();
        this.connectionFactory = new RabbitMQUtils(rabbitProperties).getConnectionFactory();
    }

    @Test
    public void testProducer() throws InterruptedException {
        int count = 100;

        SenderOptions senderOptions = new SenderOptions()
                .connectionFactory(this.connectionFactory)
                .connectionSupplier(cf -> cf.newConnection(
                        new Address[]{new Address("192.168.99.100", 5672), new Address("192.168.99.100", 5673), new Address("192.168.99.100", 5674)},
                        "reactive-sender"))
                .resourceManagementScheduler(Schedulers.elastic());
        Sender sender = RabbitFlux.createSender(senderOptions);
        Flux<OutboundMessageResult> confirmations = sender.sendWithPublishConfirms(Flux.range(1, count)
                .map(i -> new OutboundMessage("", QUEUE, ("Message_" + i).getBytes())));

        CountDownLatch latch = new CountDownLatch(count);

        sender.declareQueue(QueueSpecification.queue(QUEUE))
                .thenMany(confirmations)
                .doOnError(e -> log.error("Send failed", e))
                .subscribe(r -> {
                    if (r.isAck()) {
                        log.info("Message {} sent successfully", new String(r.getOutboundMessage().getBody()));
                        latch.countDown();
                    }
                });

        latch.await(10, TimeUnit.SECONDS);
        sender.close();
    }

    @Test
    public void testConsumer() throws InterruptedException {
        int count = 100;
        CountDownLatch latch = new CountDownLatch(count);

        SenderOptions senderOptions = new SenderOptions()
                .connectionFactory(this.connectionFactory)
                .connectionSupplier(cf -> cf.newConnection(
                        new Address[]{new Address("192.168.99.100", 5672), new Address("192.168.99.100", 5673), new Address("192.168.99.100", 5674)},
                        "reactive-sender"))
                .resourceManagementScheduler(Schedulers.elastic());

        Sender sender = RabbitFlux.createSender(senderOptions);
        Mono<AMQP.Queue.DeclareOk> queueDeclaration = sender.declareQueue(QueueSpecification.queue(QUEUE));

        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(this.connectionFactory)
                .connectionSupplier(cf -> cf.newConnection(
                        new Address[]{new Address("192.168.99.100", 5672), new Address("192.168.99.100", 5673), new Address("192.168.99.100", 5674)},
                        "reactive-receiver"))
                .connectionSubscriptionScheduler(Schedulers.elastic());
        Receiver receiver = RabbitFlux.createReceiver(receiverOptions);
        Flux<Delivery> messages = receiver.consumeAutoAck(QUEUE);
        Disposable disposable = queueDeclaration.thenMany(messages).subscribe(m -> {
            log.info("Received message {}", new String(m.getBody()));
            latch.countDown();
        });

        latch.await(10, TimeUnit.SECONDS);

        disposable.dispose();
        sender.close();
        receiver.close();
    }

}
