package Bubble.bubblog.global.dto.s3.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PresignedUrlDTO {
    private final String uploadUrl;     // s3-presigned-url
    private final String fileUrl;       // 서버에서 생성한 file 식별 url
}
