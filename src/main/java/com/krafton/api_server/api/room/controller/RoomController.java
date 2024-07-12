package com.krafton.api_server.api.room.controller;

import com.krafton.api_server.api.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.krafton.api_server.api.room.dto.RoomRequest.RoomCreateRequest;
import static com.krafton.api_server.api.room.dto.RoomRequest.RoomUser;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/room")
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateRequest roomCreateRequest) {
        if (roomCreateRequest.getUserId() == null) {
            return ResponseEntity.badRequest().body("User ID cannot be null");
        }
        System.out.println("roomCreateRequest.getUserId() = " + roomCreateRequest.getUserId());
        Long roomId = roomService.createRoom(roomCreateRequest);
        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/{roomId}/join")
    public void joinRoom(@PathVariable("roomId") Long roomId, @RequestBody RoomCreateRequest request) {
        roomService.joinRoom(roomId, request);
    }

    @PostMapping("/{roomId}/exit")
    public void exitRoom(@PathVariable("roomId") Long roomId, @RequestBody RoomCreateRequest request) {
        roomService.exitRoom(roomId, request);
    }

    @GetMapping("/room/{roomId}/users")
    public ResponseEntity<List<RoomUser>> callRoomUsers(@PathVariable Long roomId) {
        List<RoomUser> roomUsers = roomService.getRoomUsers(roomId);
        return ResponseEntity.ok(roomUsers);
    }

}
