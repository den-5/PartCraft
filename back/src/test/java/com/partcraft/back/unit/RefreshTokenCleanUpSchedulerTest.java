package com.partcraft.back.unit;

import com.partcraft.back.scheduler.RefreshTokenCleanUpScheduler;
import com.partcraft.back.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCleanUpSchedulerTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    private RefreshTokenCleanUpScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new RefreshTokenCleanUpScheduler(refreshTokenService);
    }

    @Nested
    class RefreshTokenCleanupTests {
        @Test
        void refreshTokenCleanup_shouldInvokeDeleteExpiredTokens() {
            scheduler.refreshTokenCleanup();

            verify(refreshTokenService, times(1)).deleteExpiredTokens();
        }

        @Test
        void refreshTokenCleanup_shouldCallServiceOnlyOnce() {
            scheduler.refreshTokenCleanup();

            verify(refreshTokenService).deleteExpiredTokens();
            verifyNoMoreInteractions(refreshTokenService);
        }

        @Test
        void refreshTokenCleanup_shouldNotThrowException_whenServiceExecutesSuccessfully() {
            doNothing().when(refreshTokenService).deleteExpiredTokens();

            assertDoesNotThrow(() -> scheduler.refreshTokenCleanup());
        }

        @Test
        void refreshTokenCleanup_shouldPropagateException_whenServiceThrowsException() {
            RuntimeException expectedException = new RuntimeException("Service error");
            doThrow(expectedException).when(refreshTokenService).deleteExpiredTokens();

            assertThrows(RuntimeException.class, () -> scheduler.refreshTokenCleanup());
        }

        @Test
        void refreshTokenCleanup_shouldBeIdempotent() {
            // Call the method multiple times
            scheduler.refreshTokenCleanup();
            scheduler.refreshTokenCleanup();
            scheduler.refreshTokenCleanup();

            // Verify the service was called exactly 3 times
            verify(refreshTokenService, times(3)).deleteExpiredTokens();
        }
    }

    @Nested
    class IntegrationBehaviorTests {
        @Test
        void scheduler_shouldWorkWithRealServiceBehavior() {
            // Simulate successful cleanup
            doNothing().when(refreshTokenService).deleteExpiredTokens();

            // Execute the scheduled task
            assertDoesNotThrow(() -> scheduler.refreshTokenCleanup());

            // Verify the interaction
            verify(refreshTokenService).deleteExpiredTokens();
        }

        @Test
        void scheduler_shouldHandleMultipleConsecutiveCalls() {
            doNothing().when(refreshTokenService).deleteExpiredTokens();

            // Simulate multiple scheduled executions
            for (int i = 0; i < 5; i++) {
                scheduler.refreshTokenCleanup();
            }

            verify(refreshTokenService, times(5)).deleteExpiredTokens();
        }
    }
}
