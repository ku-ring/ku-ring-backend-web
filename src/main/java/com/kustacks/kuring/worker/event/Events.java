package com.kustacks.kuring.worker.event;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

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
