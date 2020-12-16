package io.baum.userbagservice.users.kafka

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Consumer<T>(private val messageCount: Int) {
    private val messages = mutableListOf<T?>()
    private val latch: CountDownLatch = CountDownLatch(messageCount)

    fun onMessage(value: T?) {
        messages.add(value)
        latch.countDown()
    }

    fun awaitMessages(): List<T?> {
        if(!latch.await(10, TimeUnit.SECONDS)) {
            throw AssertionError("Expected [$messageCount] messages but only received [${latch.count}]")
        }
        return messages
    }
}