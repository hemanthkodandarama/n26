package com.bobby.n26.v1.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class Utils {

    public static String format(long epocTimestamp){
        return toLocalDateTime(epocTimestamp).toString();
    }

    public static LocalDateTime toLocalDateTime(long epocTimestamp){
        return toLocalDateTime(Instant.ofEpochMilli(epocTimestamp));
    }

    public static String format(Instant instant){
        return toLocalDateTime(instant).toString();
    }

    public static LocalDateTime toLocalDateTime(Instant instant){
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
