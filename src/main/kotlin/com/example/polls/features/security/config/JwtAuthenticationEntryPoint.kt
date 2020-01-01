package com.example.polls.features.security.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    val logger: Logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)

    /*
    This method is called whenever an exception is thrown due to an unauthenticated user trying to access resource that requires
    authentication.
     */
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("Responding with unauthorized error. Message - ${authException.message}")
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.message)
    }
}