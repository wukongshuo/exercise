package com.xxw.java8.timeapi;

import java.time.LocalTime;

public class LocalTimeExercise {
    public static void main(String[] args) {
        LocalTime localTime = LocalTime.now();
        LocalTime localTime1 = LocalTime.of(10, 10, 10);
        LocalTime localTime2 = LocalTime.parse("10:10:10");

        System.out.println(localTime+"----"+localTime1+"----"+localTime2);

        //23:59:59.999999999
        LocalTime max = LocalTime.MAX;
        //00:00
        LocalTime min = LocalTime.MIN;
        System.out.println(max+"----"+min);
    }
}
