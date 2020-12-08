package io.baum.userbagservice.users.kafka

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.TestPropertySource

@EmbeddedKafka(partitions = 1, topics = ["users-topic"])
@SpringBootTest(classes = [KafkaConfig::class, Kafka::class])
@TestPropertySource(properties = ["kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}"])
class UserProducerTest {

    @Autowired
    private lateinit var kt: KafkaTemplate<String, UserRecord>

    @Autowired
    private lateinit var kafka: Kafka

    private lateinit var consumer: Consumer<UserRecord>
    private lateinit var userProducer: UserProducer

    @BeforeEach
    fun setUp() {
        consumer = kafka.listenFor(1, "users-topic")
        userProducer = UserProducer(kt)
    }

    @Test
    fun shouldSendUserRecordToKafka() {
        val userRecord = UserRecord(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )

        val id = "1"

        userProducer.publish(id, userRecord)

        val messages = consumer.awaitMessages()
        assertThat(messages)
                .hasSize(1)
                .containsExactly(userRecord)
    }
}