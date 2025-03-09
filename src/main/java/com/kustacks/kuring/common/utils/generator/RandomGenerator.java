package com.kustacks.kuring.common.utils.generator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomGenerator {
    private static Random random;
    private static final String[] NICKNAME_PREFIXES = {"쿠링이", "건덕이", "건구스"};

    public static String generateRandomNumber(int length) {
        try {
            StringBuilder sb = new StringBuilder();
            setSecureRandomInstance();
            for (int i = 0; i < length; i++) {
                sb.append(generateRandomSingleNumber(10));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomNickname(int length) {
        try {
            setSecureRandomInstance();
            return pickRandomNicknamePrefix() + generateRandomNumber(length);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setSecureRandomInstance() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstanceStrong();
    }


    private static String pickRandomNicknamePrefix() {
        int i = generateRandomSingleNumber(NICKNAME_PREFIXES.length);
        return NICKNAME_PREFIXES[i];
    }

    private static int generateRandomSingleNumber(int bound) {
        return random.nextInt(bound);
    }
}
