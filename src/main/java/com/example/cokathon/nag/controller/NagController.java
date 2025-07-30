package com.example.cokathon.nag.controller;

import com.example.cokathon.global.dto.DataResponse;
import com.example.cokathon.global.ratelimit.RateLimit;
import com.example.cokathon.nag.dto.request.NagCreateRequest;
import com.example.cokathon.nag.dto.response.NagListDto;
import com.example.cokathon.nag.dto.response.ReportResponseDto;
import com.example.cokathon.nag.enums.Category;
import com.example.cokathon.nag.service.NagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nags")
@RequiredArgsConstructor
@Tag(name = "잔소리")
public class NagController {
    private final NagService nagService;

    @GetMapping("/all")
    @Operation(summary = "전체 잔소리 리스트 조회 API", description = "전체 잔소리를 최신순으로 불러온다.")
    public ResponseEntity<DataResponse<List<NagListDto>>> getAllNags() {
        List<NagListDto> nags = nagService.getNagsAllByLatest();
        return ResponseEntity.ok(DataResponse.from(nags));
    }

    @GetMapping("/{category}/latest")
    @Operation(summary = "잔소리 최신순 리스트 조회 API", description = "카테고리별 최신순 잔소리 리스트를 불러온다.")
    public ResponseEntity<DataResponse<List<NagListDto>>> getLatestByCategory(@PathVariable Category category) {
        List<NagListDto> nags = nagService.getNagsByCategorySortedByLatest(category);
        return ResponseEntity.ok(DataResponse.from(nags));
    }

    @GetMapping("/{category}/popular")
    @Operation(summary = "잔소리 인기순 리스트 조회 API", description = "카테고리별 인기순 잔소리 리스트를 불러온다.")
    public ResponseEntity<DataResponse<List<NagListDto>>> getPopularByCategory(@PathVariable Category category) {
        List<NagListDto> nags = nagService.getNagsByCategorySortedByLikes(category);
        return ResponseEntity.ok(DataResponse.from(nags));
    }

    @GetMapping("/{id}/nag")
    @Operation(summary = "잔소리 상세 조회 API", description = "id에 해당하는 잔소리 정보를 불러온다.")
    public ResponseEntity<DataResponse<NagListDto>> getNagById(@PathVariable Long id) {
        NagListDto nagDto = nagService.getNagById(id);
        return ResponseEntity.ok(DataResponse.from(nagDto));
    }

    @PostMapping
    @Operation(summary = "잔소리 생성 API", description = "새로운 잔소리를 등록한다.")
    @RateLimit(maxRequests = 5, timeWindowSeconds = 300, keyType = "IP")
    public ResponseEntity<DataResponse<NagListDto>> createNag(@Valid @RequestBody NagCreateRequest request) {
        NagListDto createdNag = nagService.createNag(request);
        return ResponseEntity.ok(DataResponse.from(createdNag));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "잔소리 좋아요 API")
    public ResponseEntity<DataResponse<Void>> likeNag(@PathVariable Long id) {
        nagService.likeNag(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dislike")
    @Operation(summary = "잔소리 싫어요 API")
    public ResponseEntity<DataResponse<Void>> dislikeNag(@PathVariable Long id) {
        nagService.dislikeNag(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/report")
    @Operation(summary = "잔소리 신고 API", description = "신고 10회 이상시 자동 삭제된다.")
    public ResponseEntity<DataResponse<ReportResponseDto>> reportNag(@PathVariable Long id) {
        int currentReports = nagService.reportNag(id);
        return ResponseEntity.ok(DataResponse.from(new ReportResponseDto(currentReports)));
    }
}
