package com.krafton.api_server.api.game.controller;

import com.amazonaws.Response;
import com.krafton.api_server.api.auth.domain.User;
import com.krafton.api_server.api.auth.repository.UserRepository;
import com.krafton.api_server.api.game.dto.FindDiffRequest.FindDiffRequestDto;
import com.krafton.api_server.api.game.dto.FindDiffResponse;
import com.krafton.api_server.api.game.dto.FindDiffResponse.FindDiffGeneratedImageResponseDto;
import com.krafton.api_server.api.game.service.FindDiffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.krafton.api_server.api.game.dto.FindDiffRequest.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/findDiff")
@RestController
public class FindDiffController {

    private final FindDiffService findDiffService;

    @PostMapping("/start")
    public ResponseEntity<?> startFindDiff(@RequestBody FindDiffRequestDto request) {
        log.debug("Received startFindDiff request: {}", request);
        Long gameId = findDiffService.startFindDiff(request);
        return ResponseEntity.ok(gameId);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveImage(@ModelAttribute FindDiffImageRequestDto request) throws IOException {
        log.debug("Received saveImage request: {}", request);
        findDiffService.saveImage(request);
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/inpaint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveGeneratedImage(@ModelAttribute FindDiffGeneratedImageRequestDto request) throws IOException {
        log.debug("Received saveGeneratedImage request: {}", request);
        findDiffService.saveGeneratedImage(request);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/og/{gameId}/{userId}")
    public ResponseEntity<List<String>> getOriginalImages(@PathVariable("gameId") Long gameId, @PathVariable("userId") Long userId) {
        List<String> originalImages = findDiffService.getOriginalImages(gameId, userId);
        log.warn("OriginalImages : {}", originalImages);
        return ResponseEntity.ok(originalImages);
    }

    @GetMapping("/gen/{gameId}/{userId}")
    public ResponseEntity<List<FindDiffGeneratedImageResponseDto>> getGeneratedImages(@PathVariable("gameId") Long gameId, @PathVariable("userId") Long userId) {
        List<FindDiffGeneratedImageResponseDto> generatedImages = findDiffService.getGeneratedImages(gameId, userId);
        log.warn("GeneratedImages : {}", generatedImages);
        return ResponseEntity.ok(generatedImages);
    }

    @PostMapping("/score/{userId}")
    public ResponseEntity<String> updateTotalScore(@RequestBody FindDiffScoreRequestDto request) {
        findDiffService.updateScore(request);
        return ResponseEntity.ok("OK");
    }
}