package com.kustacks.kuring.alert.application.port.out;

import com.kustacks.kuring.alert.domain.Alert;

public interface AlertCommandPort {
    Alert save(Alert alert);
}
