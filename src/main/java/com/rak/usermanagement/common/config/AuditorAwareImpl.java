package com.rak.usermanagement.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Provide user name to @CreatedBy and @LastModifiedBy to capture the user
 * who created or modified the entity
 *
 * @author Mohammmed Javad
 * @version 1.0
 *
 */

public class  AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? Optional.empty() : Optional.ofNullable(authentication.getName());
    }
}
