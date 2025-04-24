package com.kustacks.kuring.common.utils.generator;

public class NicknameGenerator {
    private static final int NICKNAME_NUMBER_LENGTH = 6;
    private static final String[] NICKNAME_PREFIXES = {"쿠링이", "건덕이", "건구스"};

    public static String generateNickname() {
        // 접두사(쿠링이, 건덕이, 건구스 중 하나) + 6자리 숫자
        String prefix = pickRandomNicknamePrefix();
        String suffix = RandomGenerator.generateRandomNumber(NICKNAME_NUMBER_LENGTH);

        return prefix + suffix;
    }

    private static String pickRandomNicknamePrefix() {
        int index = RandomGenerator.generateRandomSingleNumber(NICKNAME_PREFIXES.length);
        return NICKNAME_PREFIXES[index];
    }
}
