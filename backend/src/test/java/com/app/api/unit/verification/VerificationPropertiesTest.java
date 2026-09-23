package com.app.api.unit.verification;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.app.api.verification.*;

class VerificationPropertiesTest {

    @Test
    void defaultsMatchTheEngineDefaults() {
        VerificationProperties props = new VerificationProperties();

        // If someone changes one set of defaults and forgets the other, this catches it.
        assertEquals(VerificationEngine.Config.defaults(), props.toEngineConfig());
        props.afterPropertiesSet();   // defaults are valid: must not throw
    }

    @Test
    void boundValuesFlowIntoTheEngineConfig() {
        VerificationProperties props = new VerificationProperties();
        props.getWeights().setAi(0.5);
        props.getWeights().setGeo(0.2);
        props.getWeights().setMeta(0.15);
        props.getWeights().setTime(0.15);
        props.getThresholds().setVerified(0.8);
        props.getThresholds().setNeedsReview(0.5);

        props.afterPropertiesSet();

        assertEquals(new VerificationEngine.Config(0.5, 0.2, 0.15, 0.15, 0.8, 0.5), props.toEngineConfig());
    }

    @Test
    void weightsThatDoNotSumToOneFailAtStartup() {
        VerificationProperties props = new VerificationProperties();
        props.getWeights().setAi(0.9);

        assertThrows(IllegalStateException.class, props::afterPropertiesSet);
    }

    @Test
    void thresholdsOutOfOrderFailAtStartup() {
        VerificationProperties props = new VerificationProperties();
        props.getThresholds().setVerified(0.3);
        props.getThresholds().setNeedsReview(0.6);

        assertThrows(IllegalStateException.class, props::afterPropertiesSet);
    }

    @Test
    void nonPositiveRadiusFailsAtStartup() {
        VerificationProperties props = new VerificationProperties();
        props.setGeofenceRadiusM(0);

        assertThrows(IllegalStateException.class, props::afterPropertiesSet);
    }

    @Test
    void negativeGraceAndEmptyMimeListFailAtStartup() {
        VerificationProperties grace = new VerificationProperties();
        grace.setTaskWindowGraceMinutes(-1);
        assertThrows(IllegalStateException.class, grace::afterPropertiesSet);

        VerificationProperties mime = new VerificationProperties();
        mime.setAllowedMime(List.of());
        assertThrows(IllegalStateException.class, mime::afterPropertiesSet);
    }

    @Test
    void zoneIsResolvedAndValidated() {
        VerificationProperties props = new VerificationProperties();
        props.setZone("Africa/Johannesburg");
        props.afterPropertiesSet();
        assertEquals(java.time.ZoneId.of("Africa/Johannesburg"), props.resolveZone());

        VerificationProperties bad = new VerificationProperties();
        bad.setZone("Mars/Olympus_Mons");
        assertThrows(IllegalStateException.class, bad::afterPropertiesSet);

        VerificationProperties missing = new VerificationProperties();
        missing.setZone(null);
        assertThrows(IllegalStateException.class, missing::afterPropertiesSet);
    }

    @Test
    void captureTimeSettingsMustBeSensible() {
        VerificationProperties age = new VerificationProperties();
        age.setCaptureMaxAgeMinutes(0);
        assertThrows(IllegalStateException.class, age::afterPropertiesSet);

        VerificationProperties tolerance = new VerificationProperties();
        tolerance.setExifToleranceMinutes(-1);
        assertThrows(IllegalStateException.class, tolerance::afterPropertiesSet);
    }
}
