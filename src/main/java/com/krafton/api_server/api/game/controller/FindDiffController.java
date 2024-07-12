package com.krafton.api_server.api.game.controller;

import com.krafton.api_server.api.game.dto.FindDiffRequest.FindDiffRequestDto;
import com.krafton.api_server.api.game.service.FindDiffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public List<String> getOriginalImages(@PathVariable("gameId") Long gameId, @PathVariable("userId") Long userId) {
        return findDiffService.getOriginalImages(gameId, userId);
    }

    @GetMapping("/gen/{gameId}/{userId}")
    public List<String> getGeneratedImages(@PathVariable("gameId") Long gameId, @PathVariable("userId") Long userId) {
        return findDiffService.getGeneratedImages(gameId, userId);
    }
}