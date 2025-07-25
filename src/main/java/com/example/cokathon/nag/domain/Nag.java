package com.example.cokathon.nag.domain;

import com.example.cokathon.nag.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Nag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "nag_categories", joinColumns = @JoinColumn(name = "nag_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>();

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String faceImageUrl;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private int likes = 0;

    @Column(nullable = false)
    private int dislikes = 0;

    @Column(nullable = false)
    private int reports = 0;

    private LocalDateTime createdDate;

    // 좋아요 증가
    public void addLike() {
        this.likes++;
    }

    // 싫어요 증가
    public void addDislike() {
        this.dislikes++;
    }

    // 신고 증가 및 삭제 체크
    public void addReport() {
        this.reports++;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

}
