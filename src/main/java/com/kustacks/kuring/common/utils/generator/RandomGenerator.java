package com.kustacks.kuring.common.utils.generator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomGenerator {
    private static Random random;

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

    public static int generatePositiveNumber(int bound) {
        try {
            setSecureRandomInstance();
            if (bound == 1) {
                return 1;
            }
            return generateRandomSingleNumber(bound - 1) + 1;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private static void setSecureRandomInstance() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstanceStrong();
    }

    private static int generateRandomSingleNumber(int bound) {
        return random.nextInt(bound);
    }
}
