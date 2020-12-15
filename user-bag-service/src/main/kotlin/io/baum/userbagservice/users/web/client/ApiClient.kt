package io.baum.userbagservice.users.web.client

import io.baum.userbagservice.users.error.UpstreamException
import io.baum.userbagservice.users.web.RetrieveUserApi
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
) : RetrieveUserApi {
    private val api = "http://$host:$port"

    override fun getUserById(id: String, password: String): UserModel = restTemplate.safelyGET("$api/users/$id?password=$password")
    override fun getAllUsers(): List<UserModel> = restTemplate.safelyGET("$api/users")
    override fun getAllUsersLocal(): List<UserModel> = restTemplate.safelyGET("$api/users/local")

    private inline fun <reified T> RestTemplate.safelyGET(url: String): T = try {
        this.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<T>() {}).body!!
    } catch (e: HttpStatusCodeException) {
        throw UpstreamException("Remote Instance [$api] returned an unexpected status code", e.statusCode.value())
    }
}