package com.xxw.util.base.util.junit;

import org.junit.runner.Description;

import java.util.Comparator;

public class AlphabetComparator implements Comparator<Description> {
    @Override
    public int compare(Description desc1, Description desc2) {
        return desc1.getMethodName().compareTo(desc2.getMethodName());
    }
}