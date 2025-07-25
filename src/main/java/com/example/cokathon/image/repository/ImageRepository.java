package com.example.cokathon.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cokathon.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
