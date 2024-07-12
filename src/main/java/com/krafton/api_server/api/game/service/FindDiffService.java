package com.krafton.api_server.api.game.service;

import com.krafton.api_server.api.auth.domain.User;
import com.krafton.api_server.api.game.domain.FindDiffGame;
import com.krafton.api_server.api.game.domain.FindDiffUser;
import com.krafton.api_server.api.game.dto.FindDiffRequest;
import com.krafton.api_server.api.game.dto.FindDiffRequest.FindDiffRequestDto;
import com.krafton.api_server.api.game.repository.FindDiffGameRepository;
import com.krafton.api_server.api.game.repository.FindDiffUserRepository;
import com.krafton.api_server.api.room.domain.Room;
import com.krafton.api_server.api.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class FindDiffService {

    private final RoomRepository roomRepository;
    private final FindDiffGameRepository findDiffGameRepository;
    private final FindDiffUserRepository findDiffUserRepository;

    public Long startFindDiff(FindDiffRequestDto request) {
        FindDiffGame game = findDiffGameRepository.save(new FindDiffGame());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(NoSuchElementException::new);

        for (User participant : room.getParticipants()) {
            FindDiffUser user = FindDiffUser.builder()
                    .userId(participant.getId())
                    .build();


            findDiffUserRepository.save(user);
            game.addUser(user);
        }

        return game.getId();
    }


}
