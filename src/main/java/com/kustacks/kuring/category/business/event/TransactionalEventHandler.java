package com.kustacks.kuring.category.business.event;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
public class TransactionalEventHandler {

    private final FirebaseService firebaseService;

    public TransactionalEventHandler(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollbackFirebaseRequest(RollbackEvent rollbackEvent) {

        List<String> newUserCategoryNames = rollbackEvent.getTransactionHistory().get("new");
        List<String> removedUserCategoryNames = rollbackEvent.getTransactionHistory().get("remove");
        String token = rollbackEvent.getToken();

        try {
            log.info("=== 신청한 구독 롤백 ===");
            for (String newUserCategoryName : newUserCategoryNames) {
                firebaseService.unsubscribe(token, newUserCategoryName);
                log.info(newUserCategoryName);
            }

            log.info("=== 취소한 구독 롤백 ===");
            for (String removeUserCategoryName : removedUserCategoryNames) {
                firebaseService.subscribe(token, removeUserCategoryName);
                log.info(removeUserCategoryName);
            }
        } catch(FirebaseMessagingException | InternalLogicException e) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_ROLLBACK, e);
        }
    }

}
