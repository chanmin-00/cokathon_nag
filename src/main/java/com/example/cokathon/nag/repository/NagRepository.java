package com.example.cokathon.nag.repository;

import com.example.cokathon.nag.domain.Nag;
import com.example.cokathon.nag.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NagRepository extends JpaRepository<Nag, Long> {

    @Query("SELECT n FROM Nag n JOIN n.categories c WHERE c = :category ORDER BY n.likes DESC")
    List<Nag> findByCategoryOrderByLikesDesc(@Param("category") Category category);

    @Query("SELECT n FROM Nag n JOIN n.categories c WHERE c = :category ORDER BY n.createdDate DESC")
    List<Nag> findByCategoryOrderByCreatedDateDesc(@Param("category") Category category);

    Optional<Nag> findById(Long id);

    List<Nag> findAllByOrderByCreatedDateDesc();
}
