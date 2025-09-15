package com.nearpick.app.common.config

import com.nearpick.app.common.security.principal.UserPrincipal
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.Optional

@Component("auditConfig")
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
