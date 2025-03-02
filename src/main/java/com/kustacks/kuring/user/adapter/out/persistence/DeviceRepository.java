package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.domain.Device;
import com.kustacks.kuring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByFcmToken(String fcmToken);

    @Query("SELECT d.fcmToken FROM Device d")
    List<String> findAllFcmTokens();

    @Query("SELECT d.user FROM Device d WHERE d.fcmToken = :token")
    Optional<User> findUserByFcmToken(String token);
}
