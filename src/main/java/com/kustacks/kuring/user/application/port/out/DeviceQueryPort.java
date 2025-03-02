package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.user.domain.Device;

import java.util.Optional;

public interface DeviceQueryPort {
    Optional<Device> findDeviceByToken(String fcmToken);

}
