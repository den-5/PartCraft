package com.partcraft.back.scheduler;

import com.partcraft.back.exception.GlobalExceptionHandler;
import com.partcraft.back.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCleanUpScheduler {
    private final RefreshTokenService refreshTokenService;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    public RefreshTokenCleanUpScheduler(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refreshTokenCleanup() {
        refreshTokenService.deleteExpiredTokens();
        log.info("Refresh token cleanup completed");
    }
}
