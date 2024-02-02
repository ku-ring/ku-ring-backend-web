package com.kustacks.kuring.message.application.port.in;

import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserTokenValidationCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserUnsubscribeCommand;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;

public interface FirebaseWithUserUseCase {
    void validationToken(UserTokenValidationCommand command) throws FirebaseInvalidTokenException;
    void subscribe(UserSubscribeCommand command) throws FirebaseSubscribeException;
    void unsubscribe(UserUnsubscribeCommand command) throws FirebaseUnSubscribeException;
}
