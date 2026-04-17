package movie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@SpringBootApplication
class Application

data class Greeting(val id: Long, val content: String)

@RestController
class GreetingController {
    private val counter = AtomicLong()

    @GetMapping("/greeting")
    fun greeting(@RequestParam name: String = "World") = Greeting(counter.incrementAndGet(), name)
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
