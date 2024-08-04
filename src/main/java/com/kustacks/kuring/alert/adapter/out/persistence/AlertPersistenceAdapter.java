package com.kustacks.kuring.alert.adapter.out.persistence;

import com.kustacks.kuring.admin.application.port.out.AdminAlertQueryPort;
import com.kustacks.kuring.alert.application.port.out.AlertCommandPort;
import com.kustacks.kuring.alert.application.port.out.AlertQueryPort;
import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class AlertPersistenceAdapter implements AlertCommandPort, AlertQueryPort, AdminAlertQueryPort {

    private final AlertRepository alertRepository;

    @Override
    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    @Override
    public List<Alert> findAllByStatus(AlertStatus status) {
        return alertRepository.findAllByStatus(status);
    }

    @Override
    public Optional<Alert> findByIdAndStatusForUpdate(Long id, AlertStatus status) {
        return alertRepository.findByIdAndStatusForUpdate(id, status);
    }

    @Override
    public Page<Alert> findAllAlertByPageRequest(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }
}
