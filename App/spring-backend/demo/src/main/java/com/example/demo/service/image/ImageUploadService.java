package com.example.demo.service.image;

import com.example.demo.logger.Logger;
import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * Service responsible for uploading images to AWS S3, generating pre-signed URLs,
 * and triggering image filtering.
 * Images without an applied filter are uploaded at their original quality.
 */
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final S3Client s3Client;
    private final ImageEditService imageEditService;
    private final ImageOptimizationService imageOptimizationService;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

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
            ImageOptimizationService.ImagePayload payload = imageOptimizationService.processImage(file, originalExtension, requiresFiltering);
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

    private void triggerFilterService(String key, Integer validFilterId) {
        String downloadUrl = generateDownloadUrl(key);
        String uploadUrl = generateUploadUrl(key);

        try {
            imageEditService.edit(downloadUrl, uploadUrl, validFilterId);
        } catch (Exception e) {
            Logger.warning("Image editing failed for key %s: %s", key, e.getMessage(), e);
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
}