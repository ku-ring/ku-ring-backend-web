package com.kustacks.kuring.common.env;

public interface RemotePropertyResolver {

    /**
     * 지정된 키(key)에 해당하는 프로퍼티를 찾아 targetType으로 변환하여 반환합니다.
     *
     * @param key        프로퍼티의 키
     * @param targetType 변환할 대상 클래스 타입
     * @param <T>        반환될 프로퍼티의 타입
     * @return 변환된 타입의 프로퍼티 객체. 값이 없거나 변환에 실패하면 null을 반환할 수 있습니다.
     */
    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperties(Class<T> targetType);
}
