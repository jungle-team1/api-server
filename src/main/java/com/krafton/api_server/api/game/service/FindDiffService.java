package com.krafton.api_server.api.game.service;

import com.krafton.api_server.api.auth.domain.User;
import com.krafton.api_server.api.game.domain.FindDiffGame;
import com.krafton.api_server.api.game.domain.FindDiffImage;
import com.krafton.api_server.api.game.domain.FindDiffUser;
import com.krafton.api_server.api.game.dto.FindDiffRequest.FindDiffGeneratedImageRequestDto;
import com.krafton.api_server.api.game.dto.FindDiffRequest.FindDiffImageRequestDto;
import com.krafton.api_server.api.game.dto.FindDiffRequest.FindDiffRequestDto;
import com.krafton.api_server.api.game.repository.FindDiffGameRepository;
import com.krafton.api_server.api.game.repository.FindDiffImageRepository;
import com.krafton.api_server.api.game.repository.FindDiffUserRepository;
import com.krafton.api_server.api.image.service.AwsS3Service;
import com.krafton.api_server.api.image.service.GenerateService;
import com.krafton.api_server.api.room.domain.Room;
import com.krafton.api_server.api.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class FindDiffService {

    private final RoomRepository roomRepository;
    private final FindDiffGameRepository findDiffGameRepository;
    private final FindDiffUserRepository findDiffUserRepository;
    private final FindDiffImageRepository findDiffImageRepository;
    private final AwsS3Service awsS3Service;
    private final GenerateService generateService;

    public Long startFindDiff(FindDiffRequestDto request) {

        log.debug("Starting find diff game with roomId: {}", request.getRoomId());
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID must not be null");
        }

        FindDiffGame game = findDiffGameRepository.save(new FindDiffGame());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + request.getRoomId()));

        for (User participant : room.getParticipants()) {
            FindDiffUser user = FindDiffUser.builder()
                    .userId(participant.getId())
                    .build();

            findDiffUserRepository.save(user);
            game.addUser(user);
        }

        return game.getId();
    }

    public void saveImage(FindDiffImageRequestDto request) throws IOException {
        Long userId = request.getUserId();
        FindDiffUser user = findDiffUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        String imageUrl = awsS3Service.upload(request.getImage());

        FindDiffImage image = FindDiffImage.builder()
                .originalUrl(imageUrl)
                .build();

        image.updateUser(user);
        user.updateImage(image);

        findDiffImageRepository.save(image);
    }

    public void saveGeneratedImage(FindDiffGeneratedImageRequestDto request) throws IOException {
        Long userId = request.getUserId();
        FindDiffUser user = findDiffUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        MultipartFile processedImage = generateService.processImage(
                request.getImage(),
                request.getMaskX1(),
                request.getMaskY1(),
                request.getMaskX2(),
                request.getMaskY2(),
                request.getPrompt()
        );

        String generatedUrl = awsS3Service.upload(processedImage);

        FindDiffImage image = user.getImage();

        image.updateGen(generatedUrl);
        image.updateMask(request.getMaskX1(), request.getMaskY1(),request.getMaskX2(), request.getMaskY2());

        image.updateUser(user);
        user.updateImage(image);

        findDiffImageRepository.save(image);
    }

    @Transactional(readOnly = true)
    public List<String> getOriginalImages(Long gameId, Long userId) {
        FindDiffGame game = findDiffGameRepository.findById(gameId)
                .orElseThrow(() -> new NoSuchElementException("Game not found"));

        return game.getUsers().stream()
                .filter(user -> !user.getUserId().equals(userId))
                .map(FindDiffUser::getImage)
                .filter(Objects::nonNull)
                .map(FindDiffImage::getOriginalUrl)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getGeneratedImages(Long gameId, Long userId) {
        FindDiffGame game = findDiffGameRepository.findById(gameId)
                .orElseThrow(() -> new NoSuchElementException("Game not found"));

        return game.getUsers().stream()
                .filter(user -> !user.getUserId().equals(userId))
                .map(FindDiffUser::getImage)
                .filter(Objects::nonNull)
                .map(FindDiffImage::getGeneratedUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
