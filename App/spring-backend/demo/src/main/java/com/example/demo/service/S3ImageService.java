package com.example.demo.service;

import com.example.demo.exception.FileStorageException;
import com.example.demo.logger.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageService {

    private final S3Client s3Client;
    private final ImageEditService imageEditService;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;


    public String uploadImage(MultipartFile file, Integer filterId) {

        Integer validFilterId = imageEditService.getValidFilterId(filterId);

        String extension = getExtension(file);
        String key = "images/" + UUID.randomUUID() + extension;
        Logger.info("Uploading file to S3: " + key);

        try {

            uploadStream(file.getInputStream(), file.getSize(), key, file.getContentType());
            String finalUrl = buildPublicUrl(key);

            if (validFilterId == null) {
                return finalUrl;
            }

            String downloadUrl = generateDownloadUrl(key);
            String uploadUrl = generateUploadUrl(key);

            CompletableFuture.runAsync(() -> {
                try {

                    imageEditService.edit(downloadUrl, uploadUrl, validFilterId);
                } catch (Exception e) {
                    Logger.severe(e.getMessage());

                }
            });

            return finalUrl;

        } catch (IOException e) {
            Logger.severe(e.getMessage());
            throw new FileStorageException("Error parsing file for upload: " + e.getMessage());
        }

    }

    private void uploadStream(InputStream inputStream, long contentLength, String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (S3Exception e) {
            Logger.severe(e.getMessage());
            throw new FileStorageException("Error from S3 upload service: " + e.awsErrorDetails().errorMessage());
        }
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
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        return extension;
    }
}