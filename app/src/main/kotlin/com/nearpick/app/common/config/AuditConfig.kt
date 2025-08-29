package com.nearpick.app.common.config

import com.nearpick.app.common.user.UserPrincipal
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

@EnableJpaAuditing
@Configuration
class AuditConfig() : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val data = Optional.ofNullable(SecurityContextHolder.getContext())
            .map { it.authentication }
            .filter { it.isAuthenticated && !it.name.equals("anonymousUser") }
            .map { it.principal as UserPrincipal }
            .map { it.getUserId() }
        return data
    }
}
