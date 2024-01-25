package com.kustacks.kuring.admin.presentation;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.AUTH_AUTHENTICATION_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.FEEDBACK_SEARCH_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminQueryApiV2 {

    private final UserQueryUseCase userQueryService;

    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/feedbacks")
    public ResponseEntity<BaseResponse<List<AdminFeedbacksResult>>> getFeedbacks(
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size)
    {
        List<AdminFeedbacksResult> feedbacks = userQueryService.lookupFeedbacks(page, size);
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SEARCH_SUCCESS, feedbacks));
    }

    /**
     * Root 이상만 호출 가능한 테스트 API
     * @return OK
     */
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/root")
    public ResponseEntity<BaseResponse<List<String>>> roleAdminRoot(@AuthenticationPrincipal Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, authentication.getAuthorities()));
    }

    /**
     * Client 이상만 호출 가능한 테스트 API
     * @return OK
     */
    @Secured(AdminRole.ROLE_CLIENT)
    @GetMapping("/client")
    public ResponseEntity<BaseResponse<List<String>>> roleAdminClient(@AuthenticationPrincipal Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, authentication.getAuthorities()));
    }
}
