package io.baum.userbagservice.users.topology

import org.apache.kafka.streams.StreamsBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/topologies")
class TopologyController(
        private val builder: StreamsBuilder
) {
    @GetMapping(produces = ["text/plain"])
    fun getTopology(): String = builder.build().describe().toString()
}