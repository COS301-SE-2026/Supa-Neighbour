package com.app.api.unit.verification;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.app.api.verification.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the no-EXIF and unreadable cases, which can be built in code.
 * A JPEG that really carries EXIF (camera make/model, GPS, DateTimeOriginal) can't be generated
 * here - once you have a real phone photo, drop it in src/test/resources and add a test that
 * asserts its values.
 */
class ExifServiceTest {

    private final ExifService exifService = new ExifService();

    @Test
    void jpegWithoutExifIsReadableButEmpty() throws IOException {
        BufferedImage img = new BufferedImage(64, 48, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);

        ExifService.ExifData data = exifService.extract(out.toByteArray());

        assertTrue(data.readable());
        assertFalse(data.hasCameraInfo());
        assertFalse(data.hasGps());
        assertNull(data.gpsLat());
        assertNull(data.capturedAt());
    }

    @Test
    void garbageBytesAreUnreadableNotAnException() {
        ExifService.ExifData data = exifService.extract("definitely not an image".getBytes(StandardCharsets.UTF_8));

        assertFalse(data.readable());
        assertFalse(data.hasCameraInfo());
        assertFalse(data.hasGps());
    }

    @Test
    void nullAndEmptyInputAreUnreadable() {
        assertFalse(exifService.extract(null).readable());
        assertFalse(exifService.extract(new byte[0]).readable());
    }
}