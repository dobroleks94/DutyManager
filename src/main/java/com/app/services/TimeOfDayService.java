package com.app.services;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeOfDayService {

    private final static int MORNING_RANGE_HOUR_VALUE_MIN = 7;
    private final static int MORNING_RANGE_HOUR_VALUE_MAX = 10;
    private final static int MORNING_MINUTE_VALUE = 30;

    public static boolean isMorning(){
        return (LocalDateTime.now().toLocalTime().isAfter(
                        LocalDateTime.of(
                                LocalDateTime.now().toLocalDate(),
                                LocalTime.of(MORNING_RANGE_HOUR_VALUE_MIN, MORNING_MINUTE_VALUE))
                    .toLocalTime()) )
                && LocalDateTime.now().toLocalTime().isBefore(
                        LocalDateTime.of(
                                LocalDateTime.now().toLocalDate(),
                                LocalTime.of(MORNING_RANGE_HOUR_VALUE_MAX, MORNING_MINUTE_VALUE))
                    .toLocalTime());
    }

}
