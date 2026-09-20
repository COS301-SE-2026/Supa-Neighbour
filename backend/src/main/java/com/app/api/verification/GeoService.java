package com.app.api.verification;
 
import org.springframework.stereotype.Service;



@Service
public class GeoService {
    private static final double EARTH_RADIUS_M = 6_371_000.0;

    private static final double MAX_USABLE_ACCURACY_M = 100.0;

    /**
     * Result of a geofence check.
     *
     * @param available        whether the check could be performed with usable inputs
     * @param locationVerified whether the client location is within {@code radiusM} of the task location;
     *                         only meaningful when {@code available} is {@code true}
     * @param distanceM        the computed distance in meters, or {@code null} if unavailable
     * @param radiusM          the radius (in meters) that was checked against
     */
    public record GeoCheckResult(
        boolean available,
        boolean locationVerified,
        Double distanceM,
        double radiusM
    ){
        /**
         * Creates a result indicating the check could not be performed.
         *
         * @param radiusM the radius that would have been used
         * @return an unavailable {@link GeoCheckResult} with {@code distanceM == null}
         */
        public static GeoCheckResult unavailable(double radiusM) {
            return new GeoCheckResult(false, false, null, radiusM);
        }
    }

    /**
     * Computes the great-circle distance in meters between two coordinates
     * using the Haversine formula.
     *
     * @param lat1 latitude of the first point, in decimal degrees
     * @param lng1 longitude of the first point, in decimal degrees
     * @param lat2 latitude of the second point, in decimal degrees
     * @param lng2 longitude of the second point, in decimal degrees
     * @return the distance between the two points, in meters
     */
    public double distanceMeters(
        double lat1,
        double lng1,
        double lat2,
        double lng2
    ){
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dPhi / 2.0) * Math.sin(dPhi / 2.0)
            + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLambda / 2.0) * Math.sin(dLambda / 2.0);

        a = Math.min(1.0, a);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS_M * c;
    }

    /**
     * Checks whether a client's reported location is within {@code radiusM}
     * meters of a task's location.
     *
     * <p>The check is considered unavailable when either coordinate pair is
     * missing or invalid, or when the client-reported accuracy is non-finite,
     * negative, or worse than {@link #MAX_USABLE_ACCURACY_M}.</p>
     *
     * @param clientLat       client latitude in decimal degrees, or {@code null}
     * @param clientLng       client longitude in decimal degrees, or {@code null}
     * @param clientAccuracyM client-reported horizontal accuracy in meters,
     *                        or {@code null} if not provided
     * @param taskLat         task latitude in decimal degrees, or {@code null}
     * @param taskLng         task longitude in decimal degrees, or {@code null}
     * @param radiusM         allowed radius in meters
     * @return a {@link GeoCheckResult} describing availability, verification
     *         outcome, and the computed distance
     */
    public GeoCheckResult check(
        Double clientLat, Double clientLng, Double clientAccuracyM,
        Double taskLat, Double taskLng, double radiusM
    ){
        if(!isUsable(clientLat, clientLng) || !isUsable(taskLat, taskLng)){
            return GeoCheckResult.unavailable(radiusM);
        }

        if(clientAccuracyM != null && (clientAccuracyM.isNaN() || clientAccuracyM < 0 || clientAccuracyM > MAX_USABLE_ACCURACY_M)){
            return GeoCheckResult.unavailable(radiusM);
        }

        double distance = distanceMeters(clientLat, clientLng,taskLat, taskLng);
        return new GeoCheckResult(true, distance <= radiusM, distance, radiusM);
    }


    /**
     * Determines whether a coordinate pair is usable for a distance check.
     *
     * <p>A pair is considered unusable if either value is {@code null}, not
     * finite, out of valid lat/lng range, or is the "null island" pair
     * {@code (0.0, 0.0)}.</p>
     *
     * @param lat latitude in decimal degrees
     * @param lng longitude in decimal degrees
     * @return {@code true} if both values form a valid, usable coordinate
     */
    private static boolean isUsable(Double lat, Double lng){
         if (lat == null || lng == null) {
            return false;
         }
        if (!Double.isFinite(lat) || !Double.isFinite(lng)) {
            return false;
        }
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180){
             return false;
        }
        return !(lat == 0.0 && lng == 0.0);
    }
}
