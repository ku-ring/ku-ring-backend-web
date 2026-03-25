package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.application.assembler.NotificationCommandAssembler;
import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class MessageEventHandler implements HandleMessageEventUseCase {

    private final List<NotificationCommandAssembler<? extends MessageEvent>> assemblers;
    private final SendNotificationUseCase sendNotificationUseCase;

    @Override
    public int handle(MessageEvent event) {
        if(event == null)
            throw new IllegalArgumentException("MessageEventHandler.handle(), MessageEvent가 null 입니다.");
        List<NotificationCommand> commands = findAssembler(event).assemble(event);
        return sendNotificationUseCase.sendAll(commands);
    }

    @SuppressWarnings("unchecked")
    private <E extends MessageEvent> NotificationCommandAssembler<E> findAssembler(MessageEvent event) {
        List <NotificationCommandAssembler<? extends MessageEvent>> matches = assemblers.stream()
                .filter(assembler -> assembler.supports(event))
                .toList();

        if (matches.isEmpty()) {
            throw new IllegalArgumentException("지원하지 않는 메시지 이벤트 타입: " + event.getClass().getName());
        }
        if (matches.size() > 1) {
            throw new IllegalStateException("중복 매칭된 메시지 이벤트 타입: " + event.getClass().getName());
        }

        return (NotificationCommandAssembler<E>) matches.get(0);
    }

}
