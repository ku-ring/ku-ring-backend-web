package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.application.port.in.ValidateDeviceTokenUseCase;
import com.kustacks.kuring.new_message.application.port.out.DeviceTokenValidationPort;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeviceTokenValidationService implements ValidateDeviceTokenUseCase {

    private final DeviceTokenValidationPort deviceTokenValidationPort;

    @Override
    public boolean validate(String token) {
        return deviceTokenValidationPort.validate(token);
    }
}
