package com.xxw.util.base.util;

import sun.misc.Regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTest {
    public static void main(String[] args) {

//        String s1 = "12:10";
//        String s2 = "12:10";
//        int i = s1.compareTo(s2);
//        System.out.println(i);

        Pattern pattern = Pattern.compile("a|f");
        Matcher matcher = pattern.matcher("adfadfe");
        boolean b = matcher.find();
        String group = matcher.group();
        System.out.println(b);
        System.out.println(group);

    }
}
