package com.kustacks.kuring.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.UserEnrollAlreadyExistResponseDTO;
import com.kustacks.kuring.controller.dto.UserEnrollNewlyCreatedResponseDTO;
import com.kustacks.kuring.controller.dto.UserEnrollRequestDTO;
import com.kustacks.kuring.controller.dto.UserEnrollResponseDTO;
import com.kustacks.kuring.error.APIException;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.persistence.user.User;
import com.kustacks.kuring.service.FirebaseService;
import com.kustacks.kuring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class UserController {

    private final FirebaseService firebaseService;
    private final UserService userService;

    public UserController(FirebaseService firebaseService, UserService userService) {
        this.firebaseService = firebaseService;
        this.userService = userService;
    }

    @PutMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserEnrollResponseDTO enrollFCMToken(@RequestBody UserEnrollRequestDTO requestDTO) {
        String token = requestDTO.getToken();
        log.info("등록 요청 fcm 토큰 = {}", token);

        if(token == null) {
            throw new APIException(ErrorCode.API_BAD_REQUEST);
        }

        try {
            firebaseService.verifyToken(token);
        } catch(FirebaseMessagingException e) {
            throw new APIException(ErrorCode.API_INVALID_PARAM, e);
        }

        User user = userService.enrollUserToken(token);
        if(user == null) {
            return new UserEnrollAlreadyExistResponseDTO();
        } else {
            return new UserEnrollNewlyCreatedResponseDTO();
        }
    }
}
