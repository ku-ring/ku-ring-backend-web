package com.kustacks.kuring.category.business.event;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.user.domain.UserCategory;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TransactionalEventHandler {

    private final FirebaseService firebaseService;

    public TransactionalEventHandler(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollbackFirebaseRequest(RollbackEvent rollbackEvent) {
        Map<String, List<UserCategory>> transactionHistory = rollbackEvent.getTransactionHistory();
        String token = rollbackEvent.getToken();

        List<UserCategory> newUserCategories = transactionHistory.get("new");
        List<UserCategory> removedUserCategories = transactionHistory.get("remove");

        try {
            log.info("=== 신청한 구독 롤백 ===");
            for (UserCategory newUserCategory : newUserCategories) {
                firebaseService.unsubscribe(token, newUserCategory.getCategory().getName());
                log.info(newUserCategory.getCategory().getName());
            }

            log.info("=== 취소한 구독 롤백 ===");
            for (UserCategory removeUserCategory : removedUserCategories) {
                firebaseService.subscribe(token, removeUserCategory.getCategory().getName());
                log.info(removeUserCategory.getCategory().getName());
            }
        } catch(FirebaseMessagingException | InternalLogicException e) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_ROLLBACK, e);
        }
    }

}
