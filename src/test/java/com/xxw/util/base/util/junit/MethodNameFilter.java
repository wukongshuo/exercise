package com.xxw.util.base.util.junit;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MethodNameFilter extends Filter {
    private final Set<String> excludedMethods = new HashSet<String>();

    public MethodNameFilter(String... excludedMethods) {
        for (String method : excludedMethods) {
            this.excludedMethods.add(method);
        }
    }

    @Override
    public boolean shouldRun(Description description) {
        String methodName = description.getMethodName();
        if (excludedMethods.contains(methodName)) {
            return false;
        }
        return true;
    }

    @Override
    public String describe() {
        return this.getClass().getSimpleName() + "-excluded methods: " +
                excludedMethods;
    }
}

