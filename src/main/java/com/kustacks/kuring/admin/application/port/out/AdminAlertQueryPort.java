package com.kustacks.kuring.admin.application.port.out;

import com.kustacks.kuring.alert.domain.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminAlertQueryPort {
    Page<Alert> findAllAlertByPageRequest(Pageable pageable);
}
