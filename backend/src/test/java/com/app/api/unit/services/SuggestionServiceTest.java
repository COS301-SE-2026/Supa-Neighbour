package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.api.services.SuggestionService;
import com.app.api.services.SuggestionService.Severity;
import com.app.api.services.SuggestionService.SuggestedAction;
import com.app.api.services.SuggestionService.ViolationType;

public class SuggestionServiceTest {
    private SuggestionService suggestionService;

    @BeforeEach 
    void setUp() {
        suggestionService = new SuggestionService();

    }


    @Test
    void harassment_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.HARASSMENT,
                Severity.MINOR
            )
        );
    }

    @Test
    void harassment_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.HARASSMENT,
                Severity.MODERATE
            )
        );
    }

    @Test
    void harassment_severe_returnsSuspend30Days() {
        assertEquals(
            SuggestedAction.SUSPEND_30D,
            suggestionService.getSuggestedAction(
                ViolationType.HARASSMENT,
                Severity.SEVERE
            )
        );
    }

    @Test
    void hateSpeech_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.HATE_SPEECH,
                Severity.MINOR
            )
        );
    }

    @Test
    void hateSpeech_moderate_returnsSuspend14Days() {
        assertEquals(
            SuggestedAction.SUSPEND_14D,
            suggestionService.getSuggestedAction(
                ViolationType.HATE_SPEECH,
                Severity.MODERATE
            )
        );
    }

    @Test
    void hateSpeech_severe_returnsBan() {
        assertEquals(
            SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.HATE_SPEECH,
                Severity.SEVERE
            )
        );
    }


    @Test
    void inappropriateContent_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.INAPPROPRIATE_CONTENT,
                Severity.MINOR
            )
        );
    }

    @Test
    void inappropriateContent_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.INAPPROPRIATE_CONTENT,
                Severity.MODERATE
            )
        );
    }

    @Test
    void inappropriateContent_severe_returnsBan() {
        assertEquals(
            SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.INAPPROPRIATE_CONTENT,
                Severity.SEVERE
            )
        );
    }

    @Test 
    void spamScam_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.SPAM_SCAM,
                Severity.MINOR
            )
        );
    }

    @Test 
    void spamScam_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(ViolationType.SPAM_SCAM, Severity.MODERATE)
        );
    }

    @Test 
    void spamScam_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(ViolationType.SPAM_SCAM,Severity.SEVERE)
        );
    }

    //privacy
        @Test 
    void privacyViolation_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.PRIVACY_VIOLATION,
                Severity.MINOR
            )
        );
    }

    @Test 
    void privacyViolation_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_30D,
            suggestionService.getSuggestedAction(
                ViolationType.PRIVACY_VIOLATION, Severity.MODERATE)
        );
    }

    @Test 
    void privacyViolation_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.PRIVACY_VIOLATION,Severity.SEVERE)
        );
    }

        //impersion
        @Test 
    void impersonation_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.IMPERSONATION,
                Severity.MINOR
            )
        );
    }

    @Test 
    void impersonation_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_14D,
            suggestionService.getSuggestedAction(
                ViolationType.IMPERSONATION, 
                Severity.MODERATE)
        );
    }

    @Test 
    void impersonation_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.IMPERSONATION,
                Severity.SEVERE)
        );
    }

        //task no show
        @Test 
    void taskNoShow_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_NO_SHOW,
                Severity.MINOR
            )
        );
    }

    @Test 
    void taskNoShow_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_NO_SHOW, 
                Severity.MODERATE)
        );
    }

    @Test 
    void taskNoShow_severe_returnsBan() {
        assertEquals(SuggestedAction.SUSPEND_30D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_NO_SHOW,
                Severity.SEVERE)
        );
    }

        //poor quality
        @Test 
    void taskPoorQuality_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_POOR_QUALITY,
                Severity.MINOR
            )
        );
    }

    @Test 
    void taskPoorQuality_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_30D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_POOR_QUALITY, 
                Severity.MODERATE)
        );
    }

    @Test 
    void taskPoorQuality_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_POOR_QUALITY,
                Severity.SEVERE)
        );
    }

        //property damage
        @Test 
    void taskPropertyDamage_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_PROPERTY_DAMAGE,
                Severity.MINOR
            )
        );
    }

    @Test 
    void taskPropertyDamage_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_30D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_PROPERTY_DAMAGE, 
                Severity.MODERATE)
        );
    }

    @Test 
    void taskUnsafeConditions_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_PROPERTY_DAMAGE,
                Severity.SEVERE)
        );
    }

            //unsafe conditions 
        @Test 
    void taskUnsafeConditions_minor_returnsWarning() {
        assertEquals(
            SuggestedAction.WARNING,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_UNSAFE_CONDITIONS,
                Severity.MINOR
            )
        );
    }

    @Test 
    void taskUnsafeConditions_moderate_returnsSuspend7Days() {
        assertEquals(
            SuggestedAction.SUSPEND_7D,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_UNSAFE_CONDITIONS, 
                Severity.MODERATE)
        );
    }

    @Test 
    void taskPropertyDamage_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.TASK_PROPERTY_DAMAGE,
                Severity.SEVERE)
        );
    }

                //threats 
    @Test 
    void threatsViolence_severe_returnsBan() {
        assertEquals(SuggestedAction.BAN,
            suggestionService.getSuggestedAction(
                ViolationType.THREATS_VIOLENCE,
                Severity.SEVERE)
        );
    }
    
                //nulls
    @Test
    void nullViolationType_returnsNull() {
        assertNull(
            suggestionService.getSuggestedAction(null, Severity.SEVERE)
        );
    }

    @Test 
    void nullSeverity_returnsNull() {
        assertNull(
            suggestionService.getSuggestedAction(
                ViolationType.HARASSMENT,
                null
            )
        );
    }

    @Test 
    void bothViolationTypeAndSeverityNull_returnsNull() {
        assertNull(
            suggestionService.getSuggestedAction(
                null,
                null
            )
        );
    }
}


