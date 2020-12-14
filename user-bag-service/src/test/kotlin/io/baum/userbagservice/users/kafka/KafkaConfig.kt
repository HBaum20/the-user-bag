package io.baum.userbagservice.users.kafka

import io.baum.userbagservice.users.kafka.serializer.UserDeserializer
import io.baum.userbagservice.users.kafka.serializer.UserSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.test.EmbeddedKafkaBroker
import java.util.concurrent.CountDownLatch

@Configuration
@EnableKafka
class KafkaConfig {

    lateinit var received: String
    lateinit var batchReceived: String

    val latch = CountDownLatch(2)

    @Value("\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}")
    private lateinit var brokerAddresses: String

    @Bean
    fun kafkaProducerFactory(): ProducerFactory<String, UserRecord> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = this.brokerAddresses
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = UserSerializer::class.java
        return DefaultKafkaProducerFactory(configs)
    }

    @Bean
    fun kafkaConsumerFactory(): ConsumerFactory<String, UserRecord> {
        val configs = HashMap<String, Any>()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = this.brokerAddresses
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = UserDeserializer::class.java
        configs[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        configs[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        configs[ConsumerConfig.GROUP_ID_CONFIG] = "user-bag"
        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun kafkaTemplate(kafkaProducerFactory: ProducerFactory<String, UserRecord>): KafkaTemplate<String, UserRecord> {
        return KafkaTemplate(kafkaProducerFactory)
    }

    @Bean
    fun kafkaListenerContainerFactory(kafkaConsumerFactory: ConsumerFactory<String, UserRecord>): ConcurrentKafkaListenerContainerFactory<String, UserRecord> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, UserRecord>
                = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = kafkaConsumerFactory
        return factory
    }

    @KafkaListener(id = "kotlin", topics = ["users-topic"], containerFactory = "kafkaListenerContainerFactory")
    fun listen(value: String) {
        this.received = value
        this.latch.countDown()
    }

}