package io.baum.userbagservice.users.error

data class UpstreamException(override val message: String?, val status: Int) : Exception(message)
data class KafkaMetadataNotFoundException(override val message: String?) : Exception(message)
data class UnauthorisedUserException(val userId: String) : Exception("Incorrect password for user [$userId]")
data class RecordNotFoundException(val userId: String) : Exception("No user record exists for ID [$userId]")