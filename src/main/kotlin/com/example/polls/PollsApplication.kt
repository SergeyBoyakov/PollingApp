package com.example.polls

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
@EntityScan(basePackageClasses = [PollsApplication::class, Jsr310JpaConverters::class])
class PollsApplication {

    @PostConstruct
    fun init() = TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

}

fun main(args: Array<String>) {
    runApplication<PollsApplication>(*args)
}
