package com.app.api.unit.verification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.app.api.verification.*;

class GeoServiceTest {

    // No mocks needed - GeoService is pure maths.
    private final GeoService geoService = new GeoService();

    private static final double TASK_LAT = -25.7545;
    private static final double TASK_LNG = 28.2314;
    private static final double RADIUS = 75.0;

    @Test
    void samePointIsZeroMetres() {
        assertEquals(0.0, geoService.distanceMeters(TASK_LAT, TASK_LNG, TASK_LAT, TASK_LNG), 1e-6);
    }

    @Test
    void oneDegreeOfLatitudeIsAbout111Km() {
        double d = geoService.distanceMeters(10.0, 20.0, 11.0, 20.0);
        assertEquals(111_195.0, d, 5.0);
    }

    @Test
    void distanceIsSymmetric() {
        double ab = geoService.distanceMeters(-25.7, 28.2, -26.2, 28.0);
        double ba = geoService.distanceMeters(-26.2, 28.0, -25.7, 28.2);
        assertEquals(ab, ba, 1e-6);
    }

    @Test
    void closeFixIsInsideGeofence() {
        // 0.0005 degrees of latitude is roughly 55 m
        GeoService.GeoCheckResult r = geoService.check(TASK_LAT + 0.0005, TASK_LNG, 10.0, TASK_LAT, TASK_LNG, RADIUS);
        assertTrue(r.available());
        assertTrue(r.locationVerified());
        assertEquals(55.6, r.distanceM(), 1.0);
        assertEquals(RADIUS, r.radiusM(), 1e-9);
    }

    @Test
    void farFixIsOutsideGeofence() {
        // 0.001 degrees of latitude is roughly 111 m
        GeoService.GeoCheckResult r = geoService.check(TASK_LAT + 0.001, TASK_LNG, 10.0, TASK_LAT, TASK_LNG, RADIUS);
        assertTrue(r.available());
        assertFalse(r.locationVerified());
        assertEquals(111.2, r.distanceM(), 1.0);
    }

    @Test
    void missingClientCoordinatesAreUnavailable() {
        GeoService.GeoCheckResult r = geoService.check(null, null, null, TASK_LAT, TASK_LNG, RADIUS);
        assertFalse(r.available());
        assertFalse(r.locationVerified());
        assertNull(r.distanceM());
    }

    @Test
    void missingTaskCoordinatesAreUnavailable() {
        GeoService.GeoCheckResult r = geoService.check(TASK_LAT, TASK_LNG, 10.0, null, null, RADIUS);
        assertFalse(r.available());
    }

    @Test
    void zeroZeroFixIsTreatedAsNoFix() {
        GeoService.GeoCheckResult r = geoService.check(0.0, 0.0, 10.0, TASK_LAT, TASK_LNG, RADIUS);
        assertFalse(r.available());
    }

    @Test
    void outOfRangeCoordinatesAreUnavailable() {
        assertFalse(geoService.check(91.0, 28.2, 10.0, TASK_LAT, TASK_LNG, RADIUS).available());
        assertFalse(geoService.check(-25.7, 181.0, 10.0, TASK_LAT, TASK_LNG, RADIUS).available());
        assertFalse(geoService.check(Double.NaN, 28.2, 10.0, TASK_LAT, TASK_LNG, RADIUS).available());
    }

    @Test
    void tooInaccurateFixIsUnavailable() {
        GeoService.GeoCheckResult r = geoService.check(TASK_LAT, TASK_LNG, 250.0, TASK_LAT, TASK_LNG, RADIUS);
        assertFalse(r.available());
        assertFalse(r.locationVerified());
    }

    @Test
    void unknownAccuracyIsAcceptedButKnownGoodAccuracyToo() {
        assertTrue(geoService.check(TASK_LAT, TASK_LNG, null, TASK_LAT, TASK_LNG, RADIUS).locationVerified());
        assertTrue(geoService.check(TASK_LAT, TASK_LNG, 100.0, TASK_LAT, TASK_LNG, RADIUS).locationVerified());
    }
}
