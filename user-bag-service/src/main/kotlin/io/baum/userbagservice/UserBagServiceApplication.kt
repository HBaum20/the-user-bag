package io.baum.userbagservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
class UserBagServiceApplication

fun main() {
    SpringApplication.run(UserBagServiceApplication::class.java)
}