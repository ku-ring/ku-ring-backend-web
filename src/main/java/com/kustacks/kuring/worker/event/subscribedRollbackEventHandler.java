package com.kustacks.kuring.worker.event;

import com.kustacks.kuring.common.exception.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.common.firebase.exception.FirebaseUnSubscribeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class subscribedRollbackEventHandler {

    private final FirebaseService firebaseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollbackFirebaseRequest(SubscribedRollbackEvent subscribedRollbackEvent) {
        try {
            String token = subscribedRollbackEvent.getToken();

            log.info("=== 신청한 구독 롤백 ===");
            for (String newUserCategoryName : subscribedRollbackEvent.getNewUserCategoryNames()) {
                firebaseService.unsubscribe(token, newUserCategoryName);
                log.info(newUserCategoryName);
            }

            log.info("=== 취소한 구독 롤백 ===");
            for (String removeUserCategoryName : subscribedRollbackEvent.getRemovedUserCategoryNames()) {
                firebaseService.subscribe(token, removeUserCategoryName);
                log.info(removeUserCategoryName);
            }
        } catch (FirebaseSubscribeException | FirebaseUnSubscribeException e) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_ROLLBACK, e);
        }
    }

}
