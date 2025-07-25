package com.example.cokathon.image.dto;

import com.example.cokathon.image.domain.S3Info;

public record S3InfoDTO(
		String folderName,
		String fileName,
		String url
) {
	public static S3InfoDTO of(String folderName, String fileName, String url) {
		return new S3InfoDTO(folderName, fileName, url);
	}

	public static S3InfoDTO from(S3Info s3Info) {
		return new S3InfoDTO(s3Info.getFolderName(), s3Info.getFileName(), s3Info.getUrl());
	}
}
