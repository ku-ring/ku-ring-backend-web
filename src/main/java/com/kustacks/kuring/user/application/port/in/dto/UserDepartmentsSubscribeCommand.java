package com.kustacks.kuring.user.application.port.in.dto;

import java.util.List;


public record UserDepartmentsSubscribeCommand(
        String userToken,
        List<String> departments
) {
}
