package com.app.api.unit.verification;


import org.junit.jupiter.api.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import com.app.api.verification.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashServiceTest {

    private final HashService hashService = new HashService();

    // ---------------------------------------------------------------- SHA-256

    @Test
    void sha256MatchesKnownTestVector() {
        String hash = hashService.sha256Hex("abc".getBytes(StandardCharsets.UTF_8));
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hash);
    }

    @Test
    void sha256FitsTheImageHashColumn() {
        assertEquals(64, hashService.sha256Hex(new byte[]{1, 2, 3}).length());
    }

    @Test
    void sha256DiffersWhenOneByteDiffers() {
        assertFalse(hashService.sha256Hex(new byte[]{1, 2, 3}).equals(hashService.sha256Hex(new byte[]{1, 2, 4})));
    }

    // ---------------------------------------------------------------- perceptual hash

    @Test
    void identicalImagesHaveIdenticalPerceptualHash() throws IOException {
        byte[] a = png(scene(42, 640, 480));
        byte[] b = png(scene(42, 640, 480));
        assertEquals(hashService.perceptualHash(a), hashService.perceptualHash(b));
    }

    @Test
    void recompressedImageIsANearDuplicate() throws IOException {
        BufferedImage original = scene(42, 640, 480);
        long h1 = hashService.perceptualHash(png(original));
        long h2 = hashService.perceptualHash(jpeg(original, 0.4f));

        // different bytes, same picture
        assertTrue(hashService.hammingDistance(h1, h2) <= HashService.NEAR_DUPLICATE_MAX_DISTANCE);
        assertTrue(hashService.isNearDuplicate(h1, h2));
    }

    @Test
    void resizedImageIsANearDuplicate() throws IOException {
        BufferedImage original = scene(42, 640, 480);
        long h1 = hashService.perceptualHash(png(original));
        long h2 = hashService.perceptualHash(png(scale(original, 320, 240)));
        assertTrue(hashService.isNearDuplicate(h1, h2));
    }

    @Test
    void differentPictureIsNotANearDuplicate() throws IOException {
        long h1 = hashService.perceptualHash(png(scene(42, 640, 480)));
        long h2 = hashService.perceptualHash(png(scene(7, 640, 480)));
        assertFalse(hashService.isNearDuplicate(h1, h2));
    }

    @Test
    void tinyImageDoesNotCrash() throws IOException {
        // smaller than the 9x8 comparison grid
        hashService.perceptualHash(png(scene(1, 4, 3)));
    }

    @Test
    void hammingDistanceCountsDifferingBits() {
        assertEquals(0, hashService.hammingDistance(5L, 5L));
        assertEquals(64, hashService.hammingDistance(0L, -1L));
        assertEquals(1, hashService.hammingDistance(0b1000L, 0b0000L));
    }

    @Test
    void undecodableBytesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> hashService.perceptualHash("not an image".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class, () -> hashService.perceptualHash(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> hashService.perceptualHash(null));
    }

    // ---------------------------------------------------------------- test image helpers

    /** Deterministic "scene": a 16x12 grid of random grey blocks. Same seed = same picture. */
    private static BufferedImage scene(long seed, int w, int h) {
        Random rnd = new Random(seed);
        int cols = 16;
        int rows = 12;
        int[][] grey = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grey[r][c] = rnd.nextInt(256);
            }
        }
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int g = grey[y * rows / h][x * cols / w];
                img.setRGB(x, y, (g << 16) | (g << 8) | g);
            }
        }
        return img;
    }

    private static BufferedImage scale(BufferedImage src, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    private static byte[] png(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }

    private static byte[] jpeg(BufferedImage img, float quality) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(out)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(img, null, null), param);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
