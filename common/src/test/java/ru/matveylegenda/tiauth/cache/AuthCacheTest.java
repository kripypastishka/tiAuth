package ru.matveylegenda.tiauth.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuthCacheTest {
    @AfterEach
    void cleanUp() {
        AuthCache.clearAll();
    }

    @Test
    void reconnectClearsTotpFlowButPreservesFailureBudget() {
        AuthCache.setTotpPending("Example");
        AuthCache.setPendingVerification("Example");
        assertEquals(1, AuthCache.incrementTotpAttempts("example"));

        // Обработчики отключения обеих proxy-платформ очищают только состояние проверки.
        AuthCache.clearTotpFlowState("ExAmPlE");

        assertFalse(AuthCache.isTotpPending("example"));
        assertFalse(AuthCache.isPendingVerification("example"));
        assertEquals(2, AuthCache.incrementTotpAttempts("example"));
    }

    @Test
    void explicitResetAllowsFreshTotpAttemptBudget() {
        assertEquals(1, AuthCache.incrementTotpAttempts("example"));
        assertEquals(2, AuthCache.incrementTotpAttempts("example"));

        AuthCache.resetTotpAttempts("example");

        assertEquals(1, AuthCache.incrementTotpAttempts("example"));
    }

    @Test
    void clearAllResetsTotpFailures() {
        assertEquals(1, AuthCache.incrementTotpAttempts("example"));
        AuthCache.clearAll();
        assertEquals(1, AuthCache.incrementTotpAttempts("example"));
    }
}
