package com.example.cokathon.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.example.cokathon.image.domain.Image;
import com.example.cokathon.image.dto.ImageDTO;
import com.example.cokathon.image.dto.S3InfoDTO;
import com.example.cokathon.image.dto.response.ImageIdResponse;
import com.example.cokathon.image.exception.ImageErrorCode;
import com.example.cokathon.image.exception.ImageException;
import com.example.cokathon.image.repository.ImageRepository;
import com.example.cokathon.image.service.impl.ImageServiceImpl;
import com.example.cokathon.image.service.s3.S3Uploader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

	private ImageService imageService;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private S3Uploader s3Uploader;

	private final String TEMP_FOLDER = "temp";
	private MockMultipartFile mockFile;

	@BeforeEach
	void setUp() {
		mockFile = new MockMultipartFile("file", "test.png", "image/png", "dummy".getBytes());

		imageService = new ImageServiceImpl(imageRepository, s3Uploader);
	}

	@Test
	@DisplayName("이미지를 S3에 업로드하고 DB에 저장한 후 ID를 반환한다")
	void addImageToTemp_success() {
		// given
		S3InfoDTO s3Info = S3InfoDTO.of("temp", "test.png", "https://s3.example.com/temp/test.png");
		when(s3Uploader.uploadFiles(mockFile, TEMP_FOLDER)).thenReturn(s3Info);

		Image image = Image.from(ImageDTO.from(s3Info));
		when(imageRepository.save(any(Image.class))).thenReturn(image);

		// when
		ImageIdResponse result = imageService.addImageToTemp(mockFile);

		// then
		assertThat(result).isNotNull();
		verify(s3Uploader).uploadFiles(mockFile, TEMP_FOLDER);
		verify(imageRepository).save(any(Image.class));
	}

	@Test
	@DisplayName("존재하지 않는 이미지 ID로 삭제를 시도하면 예외가 발생한다")
	void deleteImage_imageNotFound() {
		// given
		long imageId = 1L;
		when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> imageService.deleteImage(imageId))
			.isInstanceOf(ImageException.class)
			.hasMessageContaining((ImageErrorCode.IMAGE_NOT_FOUND).getMessage());

		verify(s3Uploader, never()).deleteFile(any(S3InfoDTO.class));
		verify(imageRepository, never()).delete(any(Image.class));
	}

	@Test
	@DisplayName("이미지를 삭제할 때 S3에서 파일을 삭제하고 DB에서도 제거한다")
	void deleteImage_success() {
		// given
		S3InfoDTO s3Info = S3InfoDTO.of("temp", "test.png", "https://s3.example.com/temp/test.png");
		Image image = Image.from(ImageDTO.from(s3Info));
		ReflectionTestUtils.setField(image, "id", 1L);
		when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

		// when
		imageService.deleteImage(1L);

		// then
		verify(s3Uploader).deleteFile(s3Info);
		verify(imageRepository).delete(image);
	}
}
