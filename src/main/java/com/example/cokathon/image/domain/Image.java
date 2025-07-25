package com.example.cokathon.image.domain;

import com.example.cokathon.global.entity.BaseEntity;
import com.example.cokathon.image.dto.ImageDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "images")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "s3_info")
	private S3Info s3Info;

	private Image(S3Info s3Info) {
		this.s3Info = s3Info;
	}

	public static Image from(final ImageDTO imageDTO) {
		return new Image(
			S3Info.from(imageDTO.s3InfoDTO())
		);
	}
}
