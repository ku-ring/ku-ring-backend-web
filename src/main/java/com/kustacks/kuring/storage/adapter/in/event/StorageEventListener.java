package com.kustacks.kuring.storage.adapter.in.event;

import com.kustacks.kuring.storage.adapter.in.event.dto.ClubCreateEvent;
import com.kustacks.kuring.storage.application.port.in.StorageUploadUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageEventListener {

    private final StorageUploadUseCase storageUploadUseCase;

    //동아리 생성 이벤트를 받아서 처리.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 트랜잭션 커밋 후 진행.
    public void uploadClubImages(ClubCreateEvent event) {
        storageUploadUseCase.uploadAll(event.toCommand());
    }
}
