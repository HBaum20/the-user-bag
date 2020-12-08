package io.baum.userbagservice.users.web.client

import io.baum.userbagservice.users.error.UpstreamException
import io.baum.userbagservice.users.web.model.UserModel
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate

class ApiClient(
    host: String,
    port: Int,
    private val restTemplate: RestTemplate
) {
    private val api = "http://$host:$port"

    fun getUserById(id: String, password: String): UserModel = restTemplate.safelyGET("$api/users/$id?password=$password")
    fun getAllUsers(): List<UserModel> = restTemplate.safelyGET("$api/users/remote")

    private inline fun <reified T> RestTemplate.safelyGET(url: String): T = try {
        this.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<T>() {}).body!!
    } catch (e: HttpStatusCodeException) {
        throw UpstreamException("Remote Instance [$api] returned an unexpected status code", e.statusCode.value())
    }
}