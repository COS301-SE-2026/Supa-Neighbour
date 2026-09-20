package com.app.api.verification;
 
import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import org.springframework.stereotype.Service;
 
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;


@Service
public class ExifService {
    private static final int MAX_TEXT_LENGTH = 50;

    /**
     * Extracted EXIF metadata.
     *
     * @param readable   {@code false} if the bytes could not be parsed as an
     *                   image with metadata at all
     * @param cameraMake camera manufacturer, or {@code null} if absent
     * @param cameraModel camera model, or {@code null} if absent
     * @param hasGps     whether usable GPS coordinates were present
     * @param gpsLat     latitude in decimal degrees, or {@code null} if absent
     * @param gpsLng     longitude in decimal degrees, or {@code null} if absent
     * @param capturedAt EXIF {@code DateTimeOriginal} as a wall-clock time.
     *                   EXIF stores no timezone, so this is the device's local
     *                   time <b>without</b> an offset — compare it with the
     *                   client's local capture time, not with server UTC.
     *                   {@code null} if absent.
     */
    public record ExifData(
        boolean readable, 
        String cameraMake, 
        String cameraModel,
        boolean hasGps, 
        Double gpsLat, 
        Double gpsLng, 
        LocalDateTime capturedAt
    ){
        /**
         * Creates an instance representing unparseable image bytes, with all
         * metadata fields absent.
         *
         * @return an unreadable {@link ExifData}
         */
        public static ExifData unreadable(){
            return new ExifData(false, null, null, false, null, null, null);
        }

        /**
         * Determines whether any camera identification is present.
         *
         * @return {@code true} if {@code cameraMake} or {@code cameraModel} is non-blank
         */
        public boolean hasCameraInfo(){
            return notBlank(cameraMake) || notBlank(cameraModel);
        }

        /**
         * Null-safe, whitespace-aware blank check.
         *
         * @param s the string to test; may be {@code null}
         * @return {@code true} if {@code s} is non-null and contains non-whitespace
         */
        private static boolean notBlank(String s){
            return s != null && !s.isBlank();
        }
    }

    /**
     * Extracts EXIF metadata from the given image bytes.
     *
     * <p>Never throws for bad input: {@code null}, empty, corrupt, or
     * unsupported bytes all yield {@link ExifData#unreadable()}. Individual
     * fields that are missing or invalid are returned as {@code null}/false
     * while the rest of the metadata is still populated.</p>
     *
     * @param imageBytes the encoded image bytes; may be {@code null} or empty
     * @return the extracted {@link ExifData}, or {@link ExifData#unreadable()}
     *         if the bytes could not be parsed
     */
    public ExifData extract(byte[]imageBytes){
        if(imageBytes == null || imageBytes.length == 0){
            return ExifData.unreadable();
        }
        try(InputStream in = new ByteArrayInputStream(imageBytes)){
            Metadata metadata = ImageMetadataReader.readMetadata(in);

            String  make = null;
            String model = null;
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if(ifd0 != null){
                make = clean(ifd0.getString(ExifIFD0Directory.TAG_MAKE));
                model = clean(ifd0.getString(ExifIFD0Directory.TAG_MODEL));
            }
            LocalDateTime capturedAt = null;
            ExifSubIFDDirectory sub = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if( sub != null){
                Date original = sub.getDateOriginal(TimeZone.getTimeZone("UTC"));

                if(original != null){
                    capturedAt = LocalDateTime.ofInstant(original.toInstant(), ZoneOffset.UTC);
                }
            }



            boolean hasGps = false;
            Double lat = null;
            Double lng = null;
            GpsDirectory gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            
            if (gps != null) {
                GeoLocation loc = gps.getGeoLocation();
                if (loc != null && !loc.isZero()) {
                    hasGps = true;
                    lat = loc.getLatitude();
                    lng = loc.getLongitude();
                }
            }
 
            return new ExifData(true, make, model, hasGps, lat, lng, capturedAt);
        } catch(Exception e){
            return ExifData.unreadable();
        }
    }   

    /**
     * Normalizes an EXIF string value: strips NUL characters and surrounding
     * whitespace, treats empty results as {@code null}, and truncates to
     * {@link #MAX_TEXT_LENGTH} characters.
     *
     * @param s the raw EXIF string; may be {@code null}
     * @return the cleaned value, or {@code null} if the input was {@code null}
     *         or blank after cleaning
     */
    private static String clean(String s) {
        if (s == null){
             return null;
        }
        String trimmed = s.replace("\u0000", "").trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() > MAX_TEXT_LENGTH ? trimmed.substring(0, MAX_TEXT_LENGTH) : trimmed;
    }
}
