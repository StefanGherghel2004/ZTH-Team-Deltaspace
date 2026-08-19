package com.example.demo.service;

import com.example.demo.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Iterator;
import java.util.UUID;

/**
 * Service responsible for uploading images to AWS S3, generating pre-signed URLs,
 * and triggering image filtering. Optimized for high speed and low memory footprint.
 */
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final S3Client s3Client;
    private final ImageEditService imageEditService;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    private static final int MAX_DIMENSION = 1920;
    private static final double JPEG_QUALITY = 0.75;
    private static final long COMPRESS_THRESHOLD = 200L * 1024L;

    public String upload(MultipartFile file, Integer filterId) {
        long originalSize = file.getSize();
        Logger.info("Starting image upload process. Original filename: %s, size: %d bytes",
                file.getOriginalFilename(), originalSize);

        // determine if a valid filter is requested
        boolean requiresFiltering = (filterId != null && filterId > 0);
        Integer validFilterId = requiresFiltering ? imageEditService.getValidFilterId(filterId) : null;
        requiresFiltering = (validFilterId != null);

        String originalExtension = getExtension(file);

        try {
            // prepare the payload (decide whether to optimize or skip)
            ImagePayload payload = prepareImagePayload(file, originalExtension, requiresFiltering);
            String key = "images/" + UUID.randomUUID() + payload.getExtension();

            // perform the actual upload to AWS S3
            uploadStream(payload.getInputStream(), payload.getSize(), key, payload.getContentType());
            String finalUrl = buildPublicUrl(key);
            Logger.info("Image successfully uploaded to S3 with key: %s", key);

            // return early if no filter needs to be applied
            if (!requiresFiltering) {
                return finalUrl;
            }

            // trigger the editing process in the external C# service
            triggerFilterService(key, validFilterId);

            return finalUrl;

        } catch (IOException e) {
            Logger.severe("IO error during file upload processing: %s", e.getMessage(), e);
            throw new RuntimeException("Failed to process upload file stream.", e);
        } catch (S3Exception e) {
            Logger.severe("AWS S3 error during upload: %s", e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to upload image to cloud storage.", e);
        }
    }

    /**
     * Determines whether to optimize the image or upload it exactly as received.
     */
    private ImagePayload prepareImagePayload(MultipartFile file, String originalExtension, boolean requiresFiltering) throws IOException {
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
     * Triggers the image processing in the external service.
     */
    private void triggerFilterService(String key, Integer validFilterId) {
        String downloadUrl = generateDownloadUrl(key);
        String uploadUrl = generateUploadUrl(key);

        try {
            imageEditService.edit(downloadUrl, uploadUrl, validFilterId);
        } catch (Exception e) {
            Logger.warning("Image editing failed for key %s: %s", key, e.getMessage(), e);
        }
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
                // for PNGs, preserve the format to keep transparency, only resize if necessary
                Thumbnails.of(source)
                        .size(targetWidth, targetHeight)
                        .outputFormat("png")
                        .toOutputStream(outputStream);
            } else {
                // for anything else, convert to JPEG and drop the quality to 75%
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
     * Reads only the necessary pixels from the disk/stream based on the target dimensions,
     * rather than loading the entire original image into memory.
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

            // calculate the subsampling factor (e.g., a factor of 3 means read 1 pixel, skip the next 2)
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

    private void uploadStream(InputStream inputStream, long contentLength, String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
    }

    private String generateDownloadUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private String generateUploadUrl(String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    private String buildPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    private static @NonNull String getExtension(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only images are allowed.");
        }

        String originalFilename = file.getOriginalFilename();
        return originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
    }

    /**
     * Internal payload class to transfer image data between methods.
     */
    @Getter
    @AllArgsConstructor
    private static class ImagePayload {
        private final InputStream inputStream;
        private final long size;
        private final String contentType;
        private final String extension;
    }
}