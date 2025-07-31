package com.example.cokathon.news.repository;

import com.example.cokathon.news.domain.Nag;
import com.example.cokathon.news.enums.Category;

import org.springframework.data.domain.Pageable;
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

	@Query(value = """
		SELECT * FROM nag n
		JOIN nag_categories nc ON n.id = nc.nag_id
		WHERE nc.category = :category
		ORDER BY (1.0 / (1 + n.dislikes)) + RAND()
		""", nativeQuery = true)
	List<Nag> findRandomByCategory(@Param("category") String category, Pageable pageable);

	List<Nag> findAllByOrderByCreatedDateDesc();
}
