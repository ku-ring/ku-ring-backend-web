package com.kustacks.kuring.alert.adapter.out.persistence;

import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a WHERE a.status = :status")
    List<Alert> findAllByStatus(@Param("status") AlertStatus status);

    @Query("SELECT a FROM Alert a WHERE a.id = :id and a.status = :status")
    Optional<Alert> findByIdAndStatusForUpdate(
            @Param("id") Long id,
            @Param("status") AlertStatus status
    );
}
