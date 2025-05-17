package Bubble.bubblog.global.service;

import Bubble.bubblog.domain.post.dto.res.PresignedUrlDTO;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;  // AWS SDK에서 제공하는 Presigned URL 생성기

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presigned-expiration}")
    private long presignedExpiration;

    public PresignedUrlDTO generatePresignedUrl(String fileName, String contentType) {
        try {
            String objectKey = "images/" + UUID.randomUUID() + "_" + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(putRequest)
                    .signatureDuration(Duration.ofSeconds(presignedExpiration))
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            String uploadUrl = presignedRequest.url().toString();
            String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + objectKey;

            return new PresignedUrlDTO(uploadUrl, fileUrl);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

