package io.baum.userbagservice.users.kafka

import arrow.core.Either
import io.baum.userbagservice.users.kafka.metadata.HostStoreInfo
import io.baum.userbagservice.users.kafka.metadata.MetaDataService
import io.baum.userbagservice.users.tools.toList
import io.baum.userbagservice.users.web.client.ApiClient
import io.baum.userbagservice.users.web.client.ApiClientFactory
import io.baum.userbagservice.users.web.model.RemoteAddress
import io.baum.userbagservice.users.web.model.UserModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.HostInfo
import org.apache.kafka.streams.state.KeyValueIterator
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.StreamsMetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.config.StreamsBuilderFactoryBean

class UserRepositoryTest {
    private val streamsBuilderFactoryBean = mockk<StreamsBuilderFactoryBean>()
    private val kafkaStreams = mockk<KafkaStreams>()
    private val store = mockk<ReadOnlyKeyValueStore<String, UserRecord>>()
    private val metaDataService = mockk<MetaDataService>()
    private val apiClientFactory = mockk<ApiClientFactory>()
    private val apiClient = mockk<ApiClient>()
    private val keyValueIterator = mockk<KeyValueIterator<String, UserRecord>>()

    private lateinit var underTest: UserRepository

    @BeforeEach
    fun setUp() {
        val kafkaProperties = KafkaProperties()
        kafkaProperties.streams.properties[StreamsConfig.APPLICATION_SERVER_CONFIG] = "localhost:8080"

        underTest = UserRepository(
                streamsBuilderFactoryBean,
                metaDataService,
                apiClientFactory,
                kafkaProperties
        )

        mockkStatic("io.baum.userbagservice.users.tools.ExtensionsKt")

        every { streamsBuilderFactoryBean.kafkaStreams } returns kafkaStreams

        every {
            kafkaStreams.store(
                    any<StoreQueryParameters<*>>()
            )
        } returns store
    }

    @Nested
    inner class GetByID {

        private val localhost = HostStoreInfo("localhost", 8080, true)
        private val remotehost = HostStoreInfo("localhost", 8080, false)

        @Test
        fun `should return user record for matching ID from local store`() {
            val userRecord = UserRecord(
                    "1",
                    "Arthur Morgan",
                    8000.0,
                    "arthur.morgan@skybettingandgaming.com",
                    "07714282896",
                    "hashedPassword"
            )

            every { store.get("1") } returns userRecord

            every { metaDataService.streamsMetadataForStoreAndKey(any(), eq("1"), any()) } returns localhost

            val actual = underTest.getUserById("1")

            assertThat(actual).isEqualToComparingFieldByField(Either.right(userRecord))
        }

        @Test
        fun `should return remote address if metadata for key is not local`() {
            every { metaDataService.streamsMetadataForStoreAndKey(any(), eq("1"), any()) } returns remotehost

            val actual = underTest.getUserById("1")

            assertThat(actual).isEqualToComparingFieldByField(Either.left(RemoteAddress("localhost", 8080)))
        }

        @Test
        fun `should return null when metadata for key is local and user not found locally`() {
            every { metaDataService.streamsMetadataForStoreAndKey(any(), eq("1"), any()) } returns localhost
            every { store.get("1") } returns null

            val actual = underTest.getUserById("1")

            assertThat(actual).isEqualToComparingFieldByField(Either.right(null))
        }
    }

    @Nested
    inner class GetAll {
        private val record1 = UserRecord(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "hashedPassword"
        )
        private val record2 = UserRecord(
                "2",
                "Charles Smith",
                12000.0,
                "charles.smith@skybettingandgaming.com",
                "07714282896",
                "drowssapdehsah"
        )

        private val model2 = UserModel(
                "2",
                "Charles Smith",
                12000.0,
                "charles.smith@skybettingandgaming.com",
                "07714282896",
                "drowssapdehsah"
        )

        @Test
        fun `getAllUsersLocal should return all locally stored users`() {
            val records = listOf(record1, record2)

            every { store.all() } returns keyValueIterator
            every { keyValueIterator.toList() } returns records.map { KeyValue(it.id, it) }

            val result = underTest.getAllUsersLocal()

            assertThat(result).isEqualTo(records)
        }

        @Test
        fun `getAllUsers should return all users local and remote`() {
            val records = listOf(record1)
            every { store.all() } returns keyValueIterator
            every { keyValueIterator.toList() } returns records.map { KeyValue(it.id, it) }
            every { streamsBuilderFactoryBean.kafkaStreams } returns kafkaStreams
            every {
                kafkaStreams.allMetadataForStore("encrypted-users-store")
            } returns listOf(StreamsMetadata(
                    HostInfo("localhost", 8081),
                    setOf("encrypted-users-store"),
                    setOf(
                            TopicPartition("users-topic", 0),
                            TopicPartition("users-topic", 1)
                    ),
                    setOf("encrypted-users-store"),
                    setOf(
                            TopicPartition("users-topic", 0),
                            TopicPartition("users-topic", 1)
                    )
            ))
            every {
                apiClientFactory.forInstance(RemoteAddress("localhost", 8081))
            } returns apiClient
            every { apiClient.getAllUsers() } returns listOf(model2)

            val result = underTest.getAllUsers()

            assertThat(result).isEqualTo(listOf(record1, record2))
            verify { store.all() }
            verify { apiClient.getAllUsers() }
        }
    }
}