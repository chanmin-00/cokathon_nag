package com.example.cokathon.nag.repository;

import com.example.cokathon.nag.domain.Nag;
import com.example.cokathon.nag.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NagRepository extends JpaRepository<Nag, Long> {
    List<Nag> findByCategoryOrderByCreatedDateDesc(Category category);  // 최신순
    List<Nag> findByCategoryOrderByLikesDesc(Category category);        // 인기순
    Optional<Nag> findById(Long id);
}
