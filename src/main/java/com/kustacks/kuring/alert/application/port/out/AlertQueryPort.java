package com.kustacks.kuring.alert.application.port.out;

import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;

import java.util.List;
import java.util.Optional;

public interface AlertQueryPort {
    List<Alert> findAllByStatus(AlertStatus status);

    Optional<Alert> findByIdAndStatusForUpdate(Long id, AlertStatus status);
}
