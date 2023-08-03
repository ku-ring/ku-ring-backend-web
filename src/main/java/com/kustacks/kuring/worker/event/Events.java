package com.kustacks.kuring.worker.event;

import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Events {

    private static ApplicationEventPublisher publisher;

    public static void setPublisher(ApplicationEventPublisher publisher) {
        Events.publisher = publisher;
    }

    public static void raise(Object event) {
        if (Objects.nonNull(publisher)) {
            publisher.publishEvent(event);
        }
    }
}
