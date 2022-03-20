package com.kustacks.kuring.controller;

import com.kustacks.kuring.controller.dto.NoticeTriggerResponseDTO;
import com.kustacks.kuring.controller.dto.StaffTriggerResponseDTO;
import com.kustacks.kuring.error.APIException;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.service.TriggerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/trigger")
public class TriggerController {

    private final TriggerService triggerService;

    public TriggerController(TriggerService triggerServiceImpl) {
        this.triggerService = triggerServiceImpl;
    }

    @GetMapping("/notice/{type}")
    public NoticeTriggerResponseDTO triggerNotice(@RequestHeader("Authorization") String authHeader,
                                                    @PathVariable String type
    ) {
        String[] authValues = authHeader.split(" ");
        if(authValues.length != 2) {
            throw new APIException(ErrorCode.API_TRIG_UNAUTHORIZED);
        }
        if(!("Bearer".equals(authValues[0]) && triggerService.checkAuth(authValues[1]))) {
            throw new APIException(ErrorCode.API_TRIG_UNAUTHORIZED);
        } else if(!("new".equals(type) || "modify-remove".equals(type))) {
            throw new APIException(ErrorCode.API_TRIG_INVALID_TYPE);
        } else {
            triggerService.triggerNoticeWorker(type);
        }
        return new NoticeTriggerResponseDTO();
    }

    @GetMapping("/staff/{type}")
    public StaffTriggerResponseDTO triggerStaff(@RequestHeader("Authorization") String authHeader,
                                                    @PathVariable String type
    ) {
        String[] authValues = authHeader.split(" ");
        if(authValues.length != 2) {
            throw new APIException(ErrorCode.API_TRIG_UNAUTHORIZED);
        }
        if(!("Bearer".equals(authValues[0]) && triggerService.checkAuth(authValues[1]))) {
            throw new APIException(ErrorCode.API_TRIG_UNAUTHORIZED);
        } else if(!"all".equals(type)) {
            throw new APIException(ErrorCode.API_TRIG_INVALID_TYPE);
        } else {
            triggerService.triggerStaffWorker(type);
        }
        return new StaffTriggerResponseDTO();
    }

    @GetMapping("/user/{type}")
    public StaffTriggerResponseDTO triggerUser(@RequestHeader("Authorization") String authHeader,
                                                @PathVariable String type) {
        String[] authValues = authHeader.split(" ");
        if(authValues.length != 2) {
            throw new APIException(ErrorCode.API_TRIG_UNAUTHORIZED);
        }
        if(!("Bearer".equals(authValues[0]) && triggerService.checkAuth(authValues[1]))) {
            throw new APIException(ErrorCode.API_TRIG_UNAUTHORIZED);
        } else if(!"validation".equals(type)) {
            throw new APIException(ErrorCode.API_TRIG_INVALID_TYPE);
        } else {
            triggerService.triggerUserWorker(type);
        }
        return new StaffTriggerResponseDTO();
    }
}
