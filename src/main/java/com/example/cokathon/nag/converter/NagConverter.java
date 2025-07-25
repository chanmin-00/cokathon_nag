package com.example.cokathon.nag.converter;

import com.example.cokathon.nag.domain.Nag;
import com.example.cokathon.nag.dto.request.NagCreateRequest;
import com.example.cokathon.nag.dto.response.NagListDto;

public class NagConverter {

    private NagConverter() {
    }

    public static NagListDto toNagListDto(Nag nag) {
        return NagListDto.builder()
                .id(nag.getId())
                .text(nag.getText())
                .imageUrl(nag.getImageUrl())
                .likes(nag.getLikes())
                .dislikes(nag.getDislikes())
                .reposrts(nag.getReports())
                .createdDate(nag.getCreatedDate())
                .build();
    }

    public static Nag toNag(NagCreateRequest request) {
        return Nag.builder()
                .category(request.category())
                .imageUrl(request.imageUrl())
                .text(request.text())
                .likes(0)
                .dislikes(0)
                .reports(0)
                .build();
    }
}
