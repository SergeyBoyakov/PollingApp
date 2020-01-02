package com.example.polls.features.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/*
* get jwt token from the req
* validate it
* load the user associated with the token
* pass it to spring security
 */
class JwtAuthenticationFilter(
    val jwtTokenProvider: JwtTokenProvider,
    val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {
    private val log: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request) ?: "" // clean jwt token without 'Bearer '

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                val userId = jwtTokenProvider.getUserIdFromJwt(jwt)

                val userDetails = customUserDetailsService.loadUserById(userId)

                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            log.error("Could not set user authentication in security context", ex)
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length)
        }

        return null
    }
}