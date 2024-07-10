package com.krafton.api_server.api.image.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.krafton.api_server.api.image.domain.AwsS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloud_front_url}")
    private String frontCloudUrl;

    public AwsS3 upload(MultipartFile file, String mode, String roomId, String userId) throws IOException {
        String objectKey = createObjectKey(mode, roomId, userId, file.getOriginalFilename());

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

    public List<AwsS3> listImages(String prefix) {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(prefix);

        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsRequest);
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        return objects.stream()
                .map(object -> AwsS3.builder()
                        .key(object.getKey())
                        .path(getImageUrl(object.getKey()))
                        .build())
                .collect(Collectors.toList());
    }

    public String getImageUrl(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);

    }

    private String createObjectKey(String mode, String roomId, String userId, String fileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("%s/%s/%s/%s_%s", mode, roomId, userId, timestamp, fileName);
    }

    private String getFullPath(String objectKey) {
        return frontCloudUrl + objectKey;
    }
}