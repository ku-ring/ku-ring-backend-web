package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.application.assembler.NotificationCommandAssembler;
import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
public class MessageEventHandler implements HandleMessageEventUseCase {

    private final List<NotificationCommandAssembler<? extends MessageEvent>> assemblers;
    private final SendNotificationUseCase sendNotificationUseCase;

    private final Map<Class<? extends MessageEvent>, NotificationCommandAssembler<? extends MessageEvent>> assemblerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (NotificationCommandAssembler<? extends MessageEvent> assembler : assemblers) {
            Class<? extends MessageEvent> eventType = assembler.supportEventType();

            if (assemblerMap.containsKey(eventType)) {
                throw new IllegalStateException("중복 등록된 메시지 이벤트 타입: " + eventType.getName());
            }

            assemblerMap.put(eventType, assembler);
        }
    }

    @Override
    public int handle(MessageEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("MessageEventHandler.handle(), MessageEvent가 null 입니다.");
        }

        List<NotificationCommand> commands = findAssembler(event).assemble(event);
        return sendNotificationUseCase.sendAll(commands);
    }

    @SuppressWarnings("unchecked")
    private <E extends MessageEvent> NotificationCommandAssembler<E> findAssembler(E event) {
        NotificationCommandAssembler<? extends MessageEvent> assembler = assemblerMap.get(event.getClass());

        if (assembler == null) {
            throw new IllegalArgumentException("지원하지 않는 메시지 이벤트 타입: " + event.getClass().getName());
        }

        return (NotificationCommandAssembler<E>) assembler;
    }
}