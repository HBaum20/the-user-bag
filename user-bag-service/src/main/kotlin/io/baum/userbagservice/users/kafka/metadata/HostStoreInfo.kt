package io.baum.userbagservice.users.kafka.metadata

data class HostStoreInfo(
    val host: String,
    val port: Int,
    val isThisHost: Boolean
)