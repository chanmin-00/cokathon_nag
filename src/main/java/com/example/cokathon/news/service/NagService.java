package com.example.cokathon.news.service;

import com.example.cokathon.news.converter.NagConverter;
import com.example.cokathon.news.domain.Nag;
import com.example.cokathon.news.dto.request.NagCreateRequest;
import com.example.cokathon.news.dto.response.NagListDto;
import com.example.cokathon.news.enums.Category;
import com.example.cokathon.news.exception.NagException;
import com.example.cokathon.news.repository.NagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.cokathon.news.exception.NagErrorCode.NAG_DELETED;
import static com.example.cokathon.news.exception.NagErrorCode.NAG_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NagService {
    private final NagRepository nagRepository;

    // 전체 조회
    public List<NagListDto> getNagsAllByLatest() {
        List<Nag> nags = nagRepository.findAllByOrderByCreatedDateDesc();
        return nags.stream()
                .map(NagConverter::toNagListDto)
                .collect(Collectors.toList());
    }

    // 최신순
    public List<NagListDto> getNagsByCategorySortedByLatest(Category category) {
        List<Nag> nags = nagRepository.findByCategoryOrderByCreatedDateDesc(category);
        return nags.stream()
                .map(NagConverter::toNagListDto)
                .collect(Collectors.toList());
    }

    // 인기순
    public List<NagListDto> getNagsByCategorySortedByLikes(Category category) {
        List<Nag> nags = nagRepository.findByCategoryOrderByLikesDesc(category);
        return nags.stream()
                .map(NagConverter::toNagListDto)
                .collect(Collectors.toList());
    }

    // 단건 조회
    public NagListDto getNagById(Long id) {
        Nag nag = nagRepository.findById(id)
                .orElseThrow(() -> NagException.from(NAG_NOT_FOUND));
        return NagConverter.toNagListDto(nag);
    }

    @Transactional
    public NagListDto createNag(NagCreateRequest request) {
        Nag nag = NagConverter.toNag(request);
        Nag saved = nagRepository.save(nag);
        return NagConverter.toNagListDto(saved);
    }

    @Transactional
    public void likeNag(Long nagId) {
        Nag nag = nagRepository.findById(nagId)
                .orElseThrow(() -> NagException.from(NAG_NOT_FOUND));
        nag.addLike();
    }

    @Transactional
    public void dislikeNag(Long nagId) {
        Nag nag = nagRepository.findById(nagId)
                .orElseThrow(() -> NagException.from(NAG_NOT_FOUND));
        nag.addDislike();
    }

    // 신고
    @Transactional
    public int reportNag(Long nagId) {
        Nag nag = nagRepository.findById(nagId)
                .orElseThrow(() -> NagException.from(NAG_DELETED));

        nag.addReport();

        if (nag.getReports() >= 10) {
            nagRepository.delete(nag);
        } else {
            nagRepository.save(nag);
        }

        return nag.getReports();
    }
}
