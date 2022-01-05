package com.xxw.java8.timeapi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateTimeExercise {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTime1 = LocalDateTime.of(2020, 12, 22, 10, 10, 10);
        LocalDateTime localDateTime2 = LocalTime.now().atDate(LocalDate.now());

        System.out.println(localDateTime+"----"+localDateTime1+"----"+localDateTime2);

        Instant now = Instant.now();
        System.out.println(now);
    }
}
