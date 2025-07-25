package com.example.cokathon.image.service.s3;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.cokathon.image.dto.S3InfoDTO;
import com.example.cokathon.image.exception.ImageErrorCode;
import com.example.cokathon.image.exception.ImageException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3UploaderTest {

    @InjectMocks
    private S3Uploader s3Uploader;

    @Mock
    private AmazonS3Client amazonS3;

    private final String BUCKET = "test-bucket";
    private final String FOLDER = "temp";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Uploader, "bucket", BUCKET);
    }

    @Test
    @DisplayName("MultipartFile을 업로드하고 S3InfoDTO를 반환한다")
    void uploadFiles_success() throws IOException {
        // given
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "sample.png", "image/png", "test image".getBytes());

        // mock: 업로드 이후 URL 반환
        when(amazonS3.getUrl(anyString(), anyString()))
                .thenReturn(new java.net.URL("https://s3.example.com/temp/sample.png"));

        // when
        S3InfoDTO result = s3Uploader.uploadFiles(multipartFile, FOLDER);

        // then
        assertThat(result).isNotNull();
        assertThat(result.folderName()).isEqualTo(FOLDER);
        assertThat(result.url()).contains("https://s3.example.com");

        // S3에 업로드가 실제로 호출됐는지 검증
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("S3에서 파일 삭제가 정상적으로 호출된다")
    void deleteFile_success() {
        // given
        S3InfoDTO info = S3InfoDTO.of("temp", "sample.png", "https://s3.example.com/temp/sample.png");

        // when
        s3Uploader.deleteFile(info);

        // then
        verify(amazonS3).deleteObject(BUCKET, "temp/sample.png");
    }

    @Test
    @DisplayName("확장자 추출 실패 시 예외가 발생한다")
    void extractExtension_fail() {
        // given
        MockMultipartFile noExtFile = new MockMultipartFile("file", "invalidfile", "image/png", "data".getBytes());

        // when & then
        assertThatThrownBy(() -> {
            // private method는 reflection으로 접근해야 한다.
            ReflectionTestUtils.invokeMethod(s3Uploader, "extractExtension", noExtFile);
        }).isInstanceOf(ImageException.class).hasMessageContaining(ImageErrorCode.IMAGE_NOT_FOUND.getMessage());
    }
}