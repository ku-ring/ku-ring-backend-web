package com.kustacks.kuring.message.adapter.in.event;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@DisplayName("[단위] 사용자 이벤트 리스너 테스트")
class MessageUserEventListenerTest extends IntegrationTestSupport {

    @Autowired
    private UserCommandUseCase userCommandService;

    @MockBean
    private MessageUserEventListener messageUserEventListener;

    @DisplayName("사용자는 알림을 받고싶은 공지의 카테고리를 구독할 수 있다")
    @Test
    public void subscribeEvent() {
        // given
        doNothing().when(firebaseService).subscribe(any());
        UserCategoriesSubscribeCommand command =
                new UserCategoriesSubscribeCommand(USER_FCM_TOKEN, List.of(CategoryName.NORMAL.getName()));

        // when
        userCommandService.editSubscribeCategories(command);


        // then
        Mockito.verify(messageUserEventListener).subscribeEvent(any());
    }

    @DisplayName("사용자는 알림을 받고싶은 공지의 카테고리를 구독 취소 수 있다")
    @Test
    public void unsubscribeEvent() {
        // given
        doNothing().when(firebaseService).unsubscribe(any());
        UserCategoriesSubscribeCommand command =
                new UserCategoriesSubscribeCommand(USER_FCM_TOKEN, List.of(CategoryName.BACHELOR.getName()));

        // when
        userCommandService.editSubscribeCategories(command);

        // then
        Mockito.verify(messageUserEventListener).unSubscribeEvent(any());
    }
}
