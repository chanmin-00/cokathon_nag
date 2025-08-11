package com.example.bojmate.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bojmate.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findAllByS3InfoFolderName(String folderName);

}
