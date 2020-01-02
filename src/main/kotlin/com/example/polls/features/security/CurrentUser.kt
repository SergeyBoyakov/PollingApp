package com.example.polls.features.security

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal
annotation class CurrentUser