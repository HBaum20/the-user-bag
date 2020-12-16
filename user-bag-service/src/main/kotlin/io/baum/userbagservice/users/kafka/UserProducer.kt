package io.baum.userbagservice.users.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserProducer(internal val kafkaTemplate: KafkaTemplate<String, UserRecord>) {
    private val logger = LoggerFactory.getLogger(UserProducer::class.java)

    fun publish(id: String, message: UserRecord) {
        val record = ProducerRecord(TOPIC, id, message)

        kafkaTemplate.send(record)
                .also { kafkaTemplate.flush() }
                .get()
    }

    companion object {
        const val TOPIC = "users-topic"
    }
}