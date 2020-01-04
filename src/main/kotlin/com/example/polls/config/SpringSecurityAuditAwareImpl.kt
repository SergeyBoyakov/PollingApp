package com.example.polls.config

import com.example.polls.features.security.UserPrincipal
import org.springframework.data.domain.AuditorAware
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class SpringSecurityAuditAwareImpl : AuditorAware<Long> {

    override fun getCurrentAuditor(): Optional<Long> {
        val authentication = SecurityContextHolder.getContext().authentication ?: return Optional.empty()

        if (!authentication.isAuthenticated || authentication is AnonymousAuthenticationToken) {
            return Optional.empty()
        }

        val userPrincipal = authentication.principal as UserPrincipal

        return Optional.ofNullable(userPrincipal.id)
    }

}