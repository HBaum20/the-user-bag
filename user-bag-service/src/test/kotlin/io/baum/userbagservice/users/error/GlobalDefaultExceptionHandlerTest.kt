package io.baum.userbagservice.users.error

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.*
import java.util.function.Supplier

class GlobalDefaultExceptionHandlerTest {
    private val dependency = mockk<Supplier<Throwable>>()
    private val controller = Controller(dependency)
    private val errorHandler = GlobalDefaultExceptionHandler()

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(errorHandler)
            .build()
    }

    @Test
    fun shouldCatchUpstream() {
        every { dependency.get() } returns UpstreamException("Not working", 1234)

        mockMvc.perform(get("/test"))
            .andExpect(status().`is`(500))
            .andExpect(content().json("""{"message": "Not working"}"""))
    }

    @Test
    fun shouldCatchUnauthorisedUserException() {
        every { dependency.get() } returns UnauthorisedUserException("1")

        mockMvc.perform(get("/test"))
            .andExpect(status().`is`(401))
            .andExpect(content().json("""{"message": "Incorrect password for user [1]"}"""))
    }

    @Test
    fun shouldCatchRecordNotFoundException() {
        every { dependency.get() } returns RecordNotFoundException("1")

        mockMvc.perform(get("/test"))
            .andExpect(status().`is`(404))
            .andExpect(content().json("""{"message": "No user record exists for ID [1]"}"""))
    }

    @RestController
    @RequestMapping("/test")
    class Controller(private val dependency: Supplier<Throwable>) {
        @GetMapping
        fun get(
            @RequestParam("query-param", required = false)
            query: SomeEnum?
        ): Unit = throw dependency.get()
    }

    enum class SomeEnum {
        VALUE
    }
}