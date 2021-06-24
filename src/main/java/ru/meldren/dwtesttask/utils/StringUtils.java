package ru.meldren.dwtesttask.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

/**
 * Created by Meldren on 22/06/2021
 */
@UtilityClass
public class StringUtils {

    private static final Random random = new Random();

    /*
    Возвращает строку с символами [A-Z], [a-z], [0-9] с указанной длиной
     */
    public static String getRandomString(int length) {
        final StringBuilder sb = new StringBuilder();
        random
                .ints('0', 'z')
                .filter(num -> num >= '0' && num <= '9' || num >= 'a' && num <= 'z' || num >= 'A' && num <= 'Z')
                .limit(length)
                .forEach(sb::appendCodePoint);
        return sb.toString();
    }

}
