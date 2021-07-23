package ru.meldren.dwtesttask.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

/**
 * Created by Meldren on 23/07/2021
 */
@UtilityClass
public class GlobalUtils {

    private static final Random random = new Random();

    public static int randomInt(int end) {
        return random.nextInt(end);
    }

    public static double randomDouble() {
        return random.nextDouble();
    }

    public static String getRemainingTime(int delta) {
        int s = delta % 60;
        delta /= 60;
        int m = delta % 60;
        delta /= 60;
        int h = delta % 24;
        delta /= 24;
        StringBuilder sb = new StringBuilder();
        boolean add = false;
        if (delta > 0) {
            sb.append(delta).append(":");
            add = true;
        }
        if (h > 0 || add) {
            if (h < 10) {
                sb.append(0);
            }
            sb.append(h).append(":");
            add = true;
        }
        if (m > 0 || add) {
            if (m < 10) {
                sb.append(0);
            }
            sb.append(m).append(":");
            add = true;
        }
        if (s >= 0 || add) {
            if (s < 10) {
                sb.append(0);
            }
            sb.append(s);
        }
        return sb.toString();
    }

}
