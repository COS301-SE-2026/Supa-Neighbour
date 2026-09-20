package com.app.api.verification;
 
import org.springframework.stereotype.Service;
 
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {
    private static final int NEAR_DUPLICATE_MAX_DISTANCE = 8;

    private static final int GRID_W = 9;
    private static final int GRID_H = 8;
    private static final int SAMPLES_PER_CELL_AXIS = 16;

     /**
     * Computes the SHA-256 digest of the given data and returns it as a
     * lowercase hexadecimal string.
     *
     * @param data the bytes to hash; must not be {@code null}
     * @return the 64-character lowercase hex representation of the SHA-256 digest
     * @throws IllegalArgumentException if {@code data} is {@code null}
     * @throws IllegalStateException    if SHA-256 is unavailable (should never
     *                                  happen on a compliant JDK)
     */
    public String sha256Hex(byte[] data){
        if(data == null){
            throw new IllegalArgumentException("data must not be null");
        }

        try{
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder sb = new StringBuilder(digest.length *2);
            for(byte b : digest){
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }

            return sb.toString();
        }catch(NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 is guaranteed by the JDK", e);
        }
    }

    /**
     * Computes a 63-bit perceptual hash of the given image.
     *
     * <p>The image is decoded, downsampled into a {@value #GRID_W}&times;{@value #GRID_H}
     * grayscale grid (each cell holding the average luminance of sampled
     * pixels), and then hashed by comparing horizontally adjacent cells:
     * a bit is set when the left cell is brighter than its right neighbour.
     * The result is order-sensitive and stable for visually similar images.</p>
     *
     * @param imageBytes the encoded image bytes (any format supported by
     *                   {@link ImageIO}); must not be {@code null} or empty
     * @return a 63-bit perceptual hash, packed into the low bits of a {@code long}
     * @throws IllegalArgumentException if {@code imageBytes} is {@code null},
     *                                  empty, cannot be decoded, or decodes to
     *                                  {@code null} (unsupported/corrupt format)
     */
    public long perpetualHash(byte[] imageBytes){
        if(imageBytes == null || imageBytes.length == 0){
            throw new IllegalArgumentException("Image data is empy");
        }

        BufferedImage image;

        try{
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        }catch(IOException e){
            throw new IllegalArgumentException("Could not decode image", e);
        }

        if(image == null){
            throw new IllegalArgumentException("Unsupported or currupt image data");
        }

        double [][] gray = toGrayGrid(image);
        long hash = 0L;
        for(int y = 0; y < GRID_H; y++){
            for(int x = 0; x < GRID_W - 1; x++){
                hash = (hash << 1) | (gray[y][x] > gray[y][x + 1] ? 1L : 0L);
            }
        }
        return hash;
    }

    /**
     * Computes the Hamming distance between two perceptual hashes — that is,
     * the number of bit positions in which they differ.
     *
     * @param a the first hash
     * @param b the second hash
     * @return the number of differing bits, between 0 and 64
     */
    public int hammingDistance(long a, long b){
        return Long.bitCount(a ^ b);
    }
    
    /**
     * Determines whether two perceptual hashes represent near-duplicate
     * images, i.e. their Hamming distance is at most
     * {@link #NEAR_DUPLICATE_MAX_DISTANCE}.
     *
     * @param a the first hash
     * @param b the second hash
     * @return {@code true} if the images are considered near-duplicates
     */
    public boolean isNearDuplicate(long a, long b){
        return hammingDistance(a, b) <= NEAR_DUPLICATE_MAX_DISTANCE;
    }
    
    /**
     * Downsamples an image into a {@value #GRID_W}&times;{@value #GRID_H}
     * grid of average grayscale intensities.
     *
     * <p>Each grid cell covers a proportional region of the source image and
     * is sampled at up to {@value #SAMPLES_PER_CELL_AXIS} points per axis.
     * Luminance is computed with the Rec.&nbsp;601 weights
     * ({@code 0.299R + 0.587G + 0.114B}). Cells that contain no samples are
     * assigned {@code 0}.</p>
     *
     * @param img the source image; must have positive width and height
     * @return a {@code [GRID_H][GRID_W]} array of averaged grayscale values in {@code [0, 255]}
     */
    private static double[][] toGrayGrid(BufferedImage img){
        int w = img.getWidth();
        int h = img.getHeight();

        double[][] grid = new double[GRID_H][GRID_W];

         for (int cy = 0; cy < GRID_H; cy++) {
            for (int cx = 0; cx < GRID_W; cx++) {
                int x0 = Math.min(cx * w / GRID_W, w - 1);
                int x1 = Math.min(Math.max((cx + 1) * w / GRID_W, x0 + 1), w);
                int y0 = Math.min(cy * h / GRID_H, h - 1);
                int y1 = Math.min(Math.max((cy + 1) * h / GRID_H, y0 + 1), h);
                int stepX = Math.max(1, (x1 - x0) / SAMPLES_PER_CELL_AXIS);
                int stepY = Math.max(1, (y1 - y0) / SAMPLES_PER_CELL_AXIS);
 
                double sum = 0;
                int n = 0;
                for (int y = y0; y < y1; y += stepY) {
                    for (int x = x0; x < x1; x += stepX) {
                        int rgb = img.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        sum += 0.299 * r + 0.587 * g + 0.114 * b;
                        n++;
                    }
                }
                grid[cy][cx] = n > 0 ? sum / n : 0;
            }
        }
        return grid;
    }
}
