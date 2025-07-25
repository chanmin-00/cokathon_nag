package com.example.cokathon.image.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.cokathon.global.exception.GlobalExceptionHandler;
import com.example.cokathon.image.dto.response.ImageIdResponse;
import com.example.cokathon.image.exception.ImageErrorCode;
import com.example.cokathon.image.exception.ImageException;
import com.example.cokathon.image.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private ImageService imageService;

	@BeforeEach
	void setUp() {
		ImageController imageController = new ImageController(imageService);
		mockMvc = MockMvcBuilders.standaloneSetup(imageController).setControllerAdvice(new GlobalExceptionHandler())
			.build();
		objectMapper = new ObjectMapper();
	}

	@Test
	@DisplayName("이미지 업로드 API - 성공")
	void addImageToTemp_success() throws Exception {
		// given
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png", "hello".getBytes());
		ImageIdResponse mockResponse = ImageIdResponse.from(1L);

		when(imageService.addImageToTemp(any())).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(multipart("/api/v1/image")
				.file(mockFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.imageId").value(1L));
	}

	@Test
	@DisplayName("이미지 업로드 API - 실패 (파일 처리 중 예외 발생)")
	void addImageToTemp_fail_dueToImageProcessing() throws Exception {
		// given
		MockMultipartFile mockFile = new MockMultipartFile("file", "bad.png", "image/png", "corrupted".getBytes());

		when(imageService.addImageToTemp(any()))
			.thenThrow(ImageException.from(ImageErrorCode.IMAGE_PROCESSING_FAIL));

		// when & then
		mockMvc.perform(multipart("/api/v1/image")
				.file(mockFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().is5xxServerError())
			.andExpect(jsonPath("$.code").value(ImageErrorCode.IMAGE_PROCESSING_FAIL.getCode()))
			.andExpect(jsonPath("$.message").exists());
	}

	@Test
	@DisplayName("이미지 삭제 API - 성공")
	void deleteImage_success() throws Exception {
		// given
		long imageId = 1L;
		doNothing().when(imageService).deleteImage(imageId);

		// when & then
		mockMvc.perform(delete("/api/v1/image/{imageId}", imageId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	@DisplayName("이미지 삭제 API - 실패 (존재하지 않는 이미지)")
	void deleteImage_fail_notFound() throws Exception {
		// given
		long imageId = 999L;

		doThrow(ImageException.from(ImageErrorCode.IMAGE_NOT_FOUND))
			.when(imageService).deleteImage(imageId);

		// when & then
		mockMvc.perform(delete("/api/v1/image/{imageId}", imageId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ImageErrorCode.IMAGE_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").exists());
	}
}
