package com.example.demo.service.image;

import com.example.demo.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Service responsible for in-memory image optimization, resizing, and format conversion.
 */
@Service
public class ImageOptimizationService {

    private static final int MAX_DIMENSION = 1920;
    private static final double JPEG_QUALITY = 0.75;
    private static final long COMPRESS_THRESHOLD = 200L * 1024L;

    /**
     * Determines whether to optimize the image or return it exactly as received.
     */
    public ImagePayload processImage(MultipartFile file, String originalExtension, boolean requiresFiltering) throws IOException {
        long originalSize = file.getSize();

        // check if the file is small to skip the optimization process (or if no filter is applied)
        if (!requiresFiltering || originalSize < COMPRESS_THRESHOLD) {
            Logger.info("Image size (%d bytes) is below the threshold or no filter applied. Skipping optimization.", originalSize);
            return new ImagePayload(file.getInputStream(), originalSize, file.getContentType(), originalExtension);
        }

        // if the file is large and requires a filter, apply the optimization logic
        boolean isPng = originalExtension.equalsIgnoreCase(".png");
        String finalExtension = isPng ? ".png" : ".jpg";
        String finalContentType = isPng ? "image/png" : "image/jpeg";

        byte[] optimizedBytes = optimizeImage(file.getInputStream(), isPng);
        long finalSize = optimizedBytes.length;

        Logger.info("Image optimized from %d bytes to %d bytes", originalSize, finalSize);

        return new ImagePayload(new ByteArrayInputStream(optimizedBytes), finalSize, finalContentType, finalExtension);
    }

    /**
     * Loads the image and applies the final compression and resizing rules.
     */
    private byte[] optimizeImage(InputStream input, boolean isPng) throws IOException {
        try (ImageInputStream imageInput = ImageIO.createImageInputStream(input);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // read the image using subsampling to reduce RAM and CPU usage
            BufferedImage source = readSampled(imageInput);

            // prevent small images from being forcefully enlarged (upscaled)
            int targetWidth = Math.min(source.getWidth(), MAX_DIMENSION);
            int targetHeight = Math.min(source.getHeight(), MAX_DIMENSION);

            if (isPng) {
                Thumbnails.of(source)
                        .size(targetWidth, targetHeight)
                        .outputFormat("png")
                        .toOutputStream(outputStream);
            } else {
                Thumbnails.of(source)
                        .size(targetWidth, targetHeight)
                        .outputFormat("jpg")
                        .outputQuality(JPEG_QUALITY)
                        .toOutputStream(outputStream);
            }
            return outputStream.toByteArray();
        }
    }

    /**
     * Reads only the necessary pixels from the disk/stream based on the target dimensions.
     */
    private BufferedImage readSampled(ImageInputStream input) throws IOException {
        if (input == null) throw new IllegalArgumentException("Image stream is empty.");

        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
        if (!readers.hasNext()) throw new IllegalArgumentException("Unsupported image format.");

        ImageReader reader = readers.next();
        try {
            reader.setInput(input, true, true);
            int width = reader.getWidth(0);
            int height = reader.getHeight(0);

            // calculate the subsampling factor
            int sampling = Math.max(1, (int) Math.ceil(Math.max(width, height) / (double) MAX_DIMENSION));

            ImageReadParam parameters = reader.getDefaultReadParam();
            parameters.setSourceSubsampling(sampling, sampling, 0, 0);

            BufferedImage source = reader.read(0, parameters);
            if (source == null) {
                throw new IllegalArgumentException("Could not decode image.");
            }
            return source;
        } finally {
            reader.dispose();
        }
    }

    /**
     * Public payload class to transfer image data to the upload service.
     */
    @Getter
    @AllArgsConstructor
    public static class ImagePayload {
        private final InputStream inputStream;
        private final long size;
        private final String contentType;
        private final String extension;
    }
}
