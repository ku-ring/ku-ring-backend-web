package com.kustacks.kuring.alert.adapter.out.persistence;

import com.kustacks.kuring.alert.application.port.out.AlertCommandPort;
import com.kustacks.kuring.alert.application.port.out.AlertQueryPort;
import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class AlertPersistenceAdapter implements AlertCommandPort, AlertQueryPort {

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
}
