package com.example.polls.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

// We’ll be accessing the APIs from the react client that will run on its own development server.
// To allow cross origin requests from the react client we need following configuration
@Configuration
class WebMvcConfig : WebMvcConfigurer {
    private val MAX_AGE_SECS = 3600L

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods(
                HttpMethod.HEAD.name,
                HttpMethod.OPTIONS.name,
                HttpMethod.GET.name,
                HttpMethod.POST.name,
                HttpMethod.PUT.name,
                HttpMethod.PATCH.name,
                HttpMethod.DELETE.name
            )
            .maxAge(MAX_AGE_SECS)
    }
}