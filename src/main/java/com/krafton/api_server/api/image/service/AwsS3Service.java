package com.krafton.api_server.api.image.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.krafton.api_server.api.image.domain.AwsS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloud_front_url}")
    private String frontCloudUrl;

    public AwsS3 upload(MultipartFile file, String dirName) throws IOException {
        String objectKey = createObjectKey(dirName, file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucket,
                objectKey,
                file.getInputStream(),
                objectMetadata
        );

        amazonS3.putObject(putObjectRequest);

        String fileUrl = getFullPath(objectKey);
        return AwsS3.builder()
                .key(objectKey)
                .path(fileUrl)
                .build();
    }

    public void delete(String key) {
        try {
            if (!amazonS3.doesObjectExist(bucket, key)) {
                throw new AmazonS3Exception("Object " + key + " does not exist!");
            }
            amazonS3.deleteObject(bucket, key);
        } catch (SdkClientException e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw e;
        }
    }

    private String createObjectKey(String dirName, String fileName) {
        return dirName + "/" + System.currentTimeMillis() + "_" + fileName;
    }

    private String getFullPath(String objectKey) {
        return frontCloudUrl + objectKey;
    }
}