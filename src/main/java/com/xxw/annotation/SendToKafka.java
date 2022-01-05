/**
 * Copyright (c) 2019, sutpc and/or its affiliates. All rights reserved.
 */
package com.xxw.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SendToKafka {
	/**
	 * The destination for a message created from the return value of a method.
	 */
	String[] value() default {};
	
	String condition() default "";
}
