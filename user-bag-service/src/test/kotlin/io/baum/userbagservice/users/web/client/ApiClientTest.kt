package io.baum.userbagservice.users.web.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.baum.userbagservice.users.error.UpstreamException
import io.baum.userbagservice.users.web.model.UserModel
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.web.client.RestTemplate

@SpringBootTest(
        classes = [RestConfig::class]
)
@AutoConfigureWireMock(port = 8081)
class ApiClientTest {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    private lateinit var underTest: ApiClient

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        underTest = ApiClient("localhost", 8081, restTemplate)
    }

    @Nested
    inner class GetById {
        private val user = UserModel(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )
        @Test
        fun `should return remote payload on 2xx`() {
            stubFor(
                    get(urlEqualTo("/users/1?password=password"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withJsonBody(user)
                            )
            )

            val response = underTest.getUserById("1", "password")

            assertThat(response).isEqualToComparingFieldByField(user)
        }

        @Test
        fun `should throw exception on non 2xx`() {
            stubFor(
                    get(urlEqualTo("/users/1?password=password"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(500)
                            )
            )

            assertThatThrownBy { underTest.getUserById("1", "password") }
                    .isEqualTo(
                            UpstreamException(
                                    "Remote Instance [http://localhost:8081] returned an unexpected status code",
                                    500
                            )
                    )
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return remote payload on 2xx`() {
            val users = listOf(
                    UserModel(
                            "1",
                            "Arthur Morgan",
                            8000.0,
                            "arthur.morgan@skybettingandgaming.com",
                            "07714282896",
                            "password"
                    ),
                    UserModel(
                            "2",
                            "Charles Smith",
                            12000.0,
                            "charles.smith@skybettingandgaming.com",
                            "07714282896",
                            "drowssap"
                    )
            )
            stubFor(
                    get(urlEqualTo("/users/remote"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withJsonBody(users)
                            )
            )

            val result = underTest.getAllUsers()

            assertThat(result).isEqualTo(users)
        }

        @Test
        fun `should throw exception on non 2xx`() {
            stubFor(
                    get(urlEqualTo("/users/remote"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(500)
                            )
            )

            assertThatThrownBy { underTest.getAllUsers() }
                    .isEqualTo(
                            UpstreamException(
                                    "Remote Instance [http://localhost:8081] returned an unexpected status code",
                                    500
                            )
                    )
        }
    }

    private fun ResponseDefinitionBuilder.withJsonBody(any: Any) = this
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(any))
}