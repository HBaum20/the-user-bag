package io.baum.userbagservice.users.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class Kafka(private val kt: KafkaTemplate<String, UserRecord>) {
    private val consumers = mutableMapOf<String, List<Consumer<UserRecord>>>()

    fun listenFor(messageCount: Int, onTopic: String): Consumer<UserRecord> {
        val consumer = Consumer<UserRecord>(messageCount)
        consumers[onTopic] = consumers.getOrDefault(onTopic, emptyList()) + consumer
        return consumer
    }

    @KafkaListener(topicPattern = ".+")
    fun receive(payload: ConsumerRecord<String, UserRecord>) {
        println("Message Received: topic=[${payload.topic()}] key=[${payload.key()}] value=[${payload.value()}]")
        consumers.filter { it.key == payload.topic() }.flatMap { it.value }
                .forEach { it.onMessage(payload.value()) }
    }
}