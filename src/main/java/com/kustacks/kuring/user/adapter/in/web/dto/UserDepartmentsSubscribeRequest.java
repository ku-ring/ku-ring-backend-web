package com.kustacks.kuring.user.adapter.in.web.dto;

import javax.validation.constraints.NotNull;
import java.util.List;


public record UserDepartmentsSubscribeRequest(
        @NotNull List<String> departments
) {
}
