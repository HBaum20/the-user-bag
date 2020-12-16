package io.baum.userbagservice.users.web.client

import io.baum.userbagservice.users.web.model.RemoteAddress
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ApiClientFactory(private val restTemplate: RestTemplate) {
    fun forInstance(address: RemoteAddress): ApiClient =
        ApiClient(address.host, address.port, restTemplate)
}