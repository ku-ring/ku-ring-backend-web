package com.kustacks.kuring.service;

import com.kustacks.kuring.persistence.user.User;

public interface UserService {
    User getUserByToken(String token);
    User insertUserToken(String token);

    /**
     * 유저 토큰 (FCM) 을 DB에 등록한다.
     * @param token 유저 FCM 토큰
     * @return 존재하지 않았던 토큰이었다면 새로 생성된 User 엔티티를 반환한다.
     *         이미 존재하는 토큰이라면, null을 리턴한다.
     */
    User enrollUserToken(String token);
}
