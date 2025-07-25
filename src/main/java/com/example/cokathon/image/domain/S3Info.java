package com.example.cokathon.image.domain;

import com.example.cokathon.image.dto.S3InfoDTO;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class S3Info {
	private String folderName;
	private String fileName;
	private String url;

	private S3Info(String folderName, String fileName, String url) {
		this.folderName = folderName;
		this.fileName = fileName;
		this.url = url;
	}

	public static S3Info from(S3InfoDTO s3InfoDTO) {
		return new S3Info(s3InfoDTO.folderName(), s3InfoDTO.fileName(), s3InfoDTO.url());
	}
}
