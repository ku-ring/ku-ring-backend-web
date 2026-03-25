package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.application.port.in.ManageTopicSubscriptionUseCase;
import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;
import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class TopicSubscriptionService implements ManageTopicSubscriptionUseCase {

    private final TopicSubscriptionPort topicSubscriptionGateway;

    @Override
    public void subscribe(String token, String topic) {
        try {
            topicSubscriptionGateway.subscribe(token, topic);
        } catch (MessageSubscribeException e) {
            log.error("토픽 구독 실패. topic={}",topic, e);
            throw new MessageSubscribeException(e);
        } catch (Exception e) {
            log.error("알 수 없는 이유로 토픽 구독 실패. topic={}",topic, e);
            throw new MessageSubscribeException(e);
        }
    }
    @Override
    public void unsubscribe(String token, String topic) {
        try {
            topicSubscriptionGateway.unsubscribe(token, topic);
        } catch (MessageUnSubscribeException e) {
            log.error("토픽 구독 해제 실패. topic={}", topic, e);
            throw new MessageUnSubscribeException(e);
        } catch (Exception e) {
            log.error("알 수 없는 이유로 토픽 구독 해제 실패. topic={}",topic, e);
            throw new MessageUnSubscribeException(e);
        }
    }

}
