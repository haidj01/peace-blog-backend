package org.peace.blog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peace.blog.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * ImageController
 * 이미지 업로드 관련 REST API
 */
@Slf4j
@RestController
@RequestMapping("/images")  // 실제 경로: /api/images
@RequiredArgsConstructor
public class ImageController {
    
    private final ImageService imageService;
    
    /**
     * 이미지 파일 업로드
     * POST /api/images/upload
     * 
     * Content-Type: multipart/form-data
     * 
     * @param filePart 업로드할 이미지 파일
     * @return Mono<ResponseEntity<String>> - 업로드된 이미지 URL
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadImage(
            @RequestPart("file") FilePart filePart) {
        
        log.info("POST /api/images/upload - Uploading image: {}", filePart.filename());
        
        return imageService.uploadImage(filePart)
                // 업로드 성공 시 200 OK와 이미지 URL 반환
                .map(ResponseEntity::ok)
                // 에러 발생 시 500 Internal Server Error
                .onErrorResume(error -> {
                    log.error("Error uploading image", error);
                    return Mono.just(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Image upload failed: " + error.getMessage())
                    );
                });
    }
    
    /**
     * Base64 이미지 업로드 (선택적)
     * POST /api/images/upload/base64
     * 
     * @param base64Image Base64 인코딩된 이미지 문자열
     * @return Mono<ResponseEntity<String>> - 업로드된 이미지 URL
     */
    @PostMapping("/upload/base64")
    public Mono<ResponseEntity<String>> uploadBase64Image(
            @RequestBody String base64Image) {
        
        log.info("POST /api/images/upload/base64 - Uploading base64 image");
        
        return imageService.uploadBase64Image(base64Image)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    log.error("Error uploading base64 image", error);
                    return Mono.just(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Base64 image upload failed: " + error.getMessage())
                    );
                });
    }
}
