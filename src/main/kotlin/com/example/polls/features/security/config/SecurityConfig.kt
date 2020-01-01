package com.example.polls.features.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity // enable web security in a project
@EnableGlobalMethodSecurity( // enable method level security on annotations
    securedEnabled = true, // enables the @Secured annotation to protect controller/service methods
    jsr250Enabled = true, // enables the @RolesAllowed annotation to protect controller/service methods
    prePostEnabled = true // enable complex expression based access control syntax with @PreAuthorize and @PostAuthorize
// e.g. @PreAuthorize("isAnonymous()") @PreAuthorize("hasRole('USER')")
)
class SecurityConfig(
    customUserDetailsService: CustomUserDetailsService, // provides loadUserByUsername() -> UserDetails e.g. custom UserPrincipal
    unauthorizedHandler: JwtAuthenticationEntryPoint // is used to return 401 unauthorized to clients that try to access a protected resource wo proper authentication
) : WebSecurityConfigurerAdapter() { // provides default security configurations and allows other class ot extend it and customize by override

    /*
    * reads JWT authentication token from the Authorization header for all requests
    * validates the token
    * loads the user details associated with this token
    * Sets user details to SpringSecurityContext -> Spring Security uses details for authorization checks.
    */
    @Bean
    fun jwtAuthenticationFilter() = JwtAuthenticationFilter()

    override
    fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        return authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder())
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    override
    fun authenticationManager(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override
    fun configure(http: HttpSecurity) {
        http
            .cors()
            .and()
            .csrf()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(
                "/",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js"
            ).permitAll()
            .antMatchers("/api/auth/**")
            .permitAll()
            .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
            .permitAll()
            .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
            .permitAll()
            .anyRequest()
            .authenticated()

        // Add our custom filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

}