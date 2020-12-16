package io.baum.userbagservice.users.kafka.stream

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.baum.userbagservice.users.kafka.UserProducer
import io.baum.userbagservice.users.kafka.UserRecord
import io.baum.userbagservice.users.tools.PasswordAuthenticator
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerde

class UserProcessorTest {

    private lateinit var testDriver: TopologyTestDriver
    private lateinit var userTopic: TestInputTopic<String, UserRecord>

    private val passwordAuthenticator = mockk<PasswordAuthenticator>()

    @BeforeEach
    fun setUp() {
        val builder = StreamsBuilder()

        val userSerde = JsonSerde(UserRecord::class.java, jacksonObjectMapper())

        val config = mapOf(
                StreamsConfig.APPLICATION_ID_CONFIG to "user-bag",
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "mock:1234",
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.StringSerde::class.java.name
        ).toProperties()

        val stream = UserStream(builder, userSerde).userEventStream()

        val table = UserProcessor(passwordAuthenticator, stream, userSerde)

        table.userTable()

        val topology = builder.build()

        testDriver = TopologyTestDriver(topology, config)
        userTopic = testDriver.createInputTopic("users-topic", Serdes.String().serializer(), userSerde.serializer())

        every { passwordAuthenticator.hash("password") } returns "hashedPassword"
    }

    @AfterEach
    fun tearDown() {
        testDriver.close()
    }

    @Test
    fun `user is created in store with hashed password`() {
        val inputUser = UserRecord(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )
        val userStore = testDriver.getKeyValueStore<String, UserRecord?>("encrypted-users-store")

        userTopic.pipeInput("1", inputUser)

        every { passwordAuthenticator.hash("password") } returns "hashedPassword"

        val createdNode = userStore.get("1")

        assertThat(createdNode).isEqualToComparingFieldByField(inputUser.copy(password = "hashedPassword"))
    }
}