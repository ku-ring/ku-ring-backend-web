package com.kustacks.kuring.common.utils.generator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomGenerator {
    private static Random random;

    public static String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(generateRandomSingleNumber(10));
        }
        return sb.toString();
    }

    public static int generateRandomSingleNumber(int bound) {
        try {
            setSecureRandomInstance();
            return random.nextInt(bound);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setSecureRandomInstance() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstanceStrong();
    }

}
