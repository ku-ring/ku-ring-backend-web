package com.kustacks.kuring.new_message.application.port.out;

public interface DeviceTokenValidationPort {
    boolean validate(String token);
}
