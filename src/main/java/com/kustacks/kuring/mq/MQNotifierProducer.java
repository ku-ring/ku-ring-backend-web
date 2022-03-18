package com.kustacks.kuring.mq;

import java.util.List;

public interface MQNotifierProducer<T> {

    void publish(T messageDTO);
    default void publish(List<T> messageDTOList) {
        for (T messageDTO : messageDTOList) {
            publish(messageDTO);
        }
    }
}
