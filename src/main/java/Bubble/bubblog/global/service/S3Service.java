package Bubble.bubblog.global.service;

import Bubble.bubblog.domain.post.dto.res.PresignedUrlDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
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
        String objectKey = "images/" + UUID.randomUUID() + "_" + fileName;

        // S3에 PUT 요청할 객체 생성: 버킷명, 키(파일 경로), 컨텐츠 타입을 지정
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)  // client로부터 jpg, png 등의 type을 받을지, txt 같은 파일은 거부하는 로직을 작성해야 할지 리뷰 부탁드립니다.
                .build();

        // 위의 PUT 요청 객체를 기반으로 Presigned URL을 만들기 위한 요청 객체 구성
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putRequest)
                .signatureDuration(Duration.ofSeconds(presignedExpiration))
                .build();

        // 최종적으로 S3Presigner를 사용해 Presigned URL 생성
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        //System.out.println("uploadUrl: " + presignedRequest.url()); // 🔍 이걸 Swagger 요청 후 콘솔에서 확인
        URL uploadUrl = presignedRequest.url();

        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + objectKey;

        // 실제 업로드할 URL (uploadUrl)과,
        // 업로드 후 클라이언트가 접근할 수 있는 정적 파일 URL(fileUrl)을 함께 DTO로 반환
        return new PresignedUrlDTO(uploadUrl.toString(), fileUrl);
    }
}
