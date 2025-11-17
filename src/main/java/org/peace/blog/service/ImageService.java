package org.peace.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * ImageService
 * 이미지 업로드 처리 (Mock 구현)
 * 실제로는 S3에 업로드하지만, 여기서는 Mock URL만 반환
 */
@Slf4j
@Service
public class ImageService {
    
    // Mock 이미지 저장 경로
    private static final String MOCK_IMAGE_BASE_URL = "https://mock-s3-bucket.s3.amazonaws.com/images/";
    
    /**
     * 이미지 파일 업로드 (Mock)
     * @param filePart 업로드할 파일
     * @return Mono<String> - 업로드된 이미지 URL
     */
    public Mono<String> uploadImage(FilePart filePart) {
        log.info("Uploading image: {}", filePart.filename());
        
        // 실제로는 S3에 업로드하지만, Mock으로 처리
        return Mono.fromCallable(() -> {
            // 원본 파일명 가져오기
            String originalFilename = filePart.filename();
            
            // 고유한 파일명 생성 (UUID 사용)
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            
            // Mock URL 생성
            String mockUrl = MOCK_IMAGE_BASE_URL + uniqueFilename;
            
            log.info("Image uploaded successfully (Mock): {}", mockUrl);
            
            return mockUrl;
        })
        .doOnError(error -> log.error("Error uploading image", error));
    }
    
    /**
     * Base64 이미지 업로드 (선택적)
     * @param base64Image Base64 인코딩된 이미지
     * @return Mono<String> - 업로드된 이미지 URL
     */
    public Mono<String> uploadBase64Image(String base64Image) {
        log.info("Uploading base64 image");
        
        return Mono.fromCallable(() -> {
            // 고유한 파일명 생성
            String uniqueFilename = UUID.randomUUID().toString() + ".png";
            
            // Mock URL 생성
            String mockUrl = MOCK_IMAGE_BASE_URL + uniqueFilename;
            
            log.info("Base64 image uploaded successfully (Mock): {}", mockUrl);
            
            return mockUrl;
        })
        .doOnError(error -> log.error("Error uploading base64 image", error));
    }
}
