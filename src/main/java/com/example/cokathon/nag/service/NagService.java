package com.example.cokathon.nag.service;

import com.example.cokathon.nag.converter.NagConverter;
import com.example.cokathon.nag.domain.Nag;
import com.example.cokathon.nag.dto.request.NagCreateRequest;
import com.example.cokathon.nag.dto.response.NagListDto;
import com.example.cokathon.nag.enums.Category;
import com.example.cokathon.nag.exception.NagException;
import com.example.cokathon.nag.repository.NagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.cokathon.nag.exception.NagErrorCode.NAG_DELETED;
import static com.example.cokathon.nag.exception.NagErrorCode.NAG_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NagService {
    private final NagRepository nagRepository;

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
