package com.xxw.java8.timeapi;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public class LocalDateExercise {
    public static void main(String[] args) {

        //实例化一个localData的三种方式
        LocalDate today = LocalDate.now();
        LocalDate localDate = LocalDate.of(2020, 12, 22);
        LocalDate localDate1 = LocalDate.parse("2020-12-22");//可以增加format作为入参
        System.out.println(today+"----"+localDate+"----"+localDate1);

        int year = today.getYear();
        Month month = today.getMonth();
        int monthValue = today.getMonthValue();
        int dayOfMonth = today.getDayOfMonth();
        System.out.println(year+"----"+month+"----"+monthValue+"----"+dayOfMonth);

        //当天所在月的第一天
        LocalDate with = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(with);
    }
}
