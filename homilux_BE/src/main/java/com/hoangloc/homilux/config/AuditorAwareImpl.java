package com.hoangloc.homilux.config;

import com.hoangloc.homilux.util.SecurityUtil;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtil.getCurrentUserLogin().orElse("SYSTEM"));
    }

}