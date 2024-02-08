package com.kustacks.kuring.user.adapter.in.web.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

public record UserCategoriesSubscribeRequest(
        @NotNull List<String> categories
) {
}
