package com.kustacks.kuring.admin.application.service;

import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertResponse;
import com.kustacks.kuring.admin.application.port.in.AdminQueryUseCase;
import com.kustacks.kuring.admin.application.port.out.AdminAlertQueryPort;
import com.kustacks.kuring.admin.application.port.out.AdminUserFeedbackPort;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class AdminQueryService implements AdminQueryUseCase {

    private final AdminUserFeedbackPort adminUserFeedbackPort;
    private final AdminAlertQueryPort adminAlertQueryPort;

    @Transactional(readOnly = true)
    @Override
    public List<AdminFeedbacksResult> lookupFeedbacks(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return adminUserFeedbackPort.findAllFeedbackByPageRequest(pageRequest)
                .stream()
                .map(dto -> new AdminFeedbacksResult(
                        dto.getContents(),
                        dto.getUserId(),
                        dto.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public List<AdminAlertResponse> lookupAlerts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return adminAlertQueryPort.findAllAlertByPageRequest(pageRequest)
                .stream()
                .map(alert -> AdminAlertResponse.of(
                        alert.getId(),
                        alert.getTitle(),
                        alert.getContent(),
                        alert.getStatus(),
                        alert.getAlertTime()
                ))
                .toList();
    }
}
