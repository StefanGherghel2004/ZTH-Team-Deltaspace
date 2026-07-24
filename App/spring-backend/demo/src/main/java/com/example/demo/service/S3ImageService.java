package com.example.demo.service;

import com.example.demo.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final S3Client s3Client;
    private final ImageEditService imageEditService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadImage(MultipartFile file, String filter) {
        try {
            String extension = getExtension(file);

            byte[] imageBytes;
            if (filter != null && !filter.isEmpty()) {
                imageBytes = imageEditService.edit(file, filter);
            } else {
                imageBytes = file.getBytes();
            }

            String uniqueFileName = UUID.randomUUID() + extension;

            return uploadBytes(imageBytes, uniqueFileName, file.getContentType());

        } catch (IOException e) {
            throw new FileStorageException("Error parsing file for upload: " + e.getMessage());
        }

    }

    private String uploadBytes(byte[] data, String fileName, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
        } catch (S3Exception e) {
            throw new FileStorageException("Error from S3 upload service: " + e.awsErrorDetails().errorMessage());
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
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