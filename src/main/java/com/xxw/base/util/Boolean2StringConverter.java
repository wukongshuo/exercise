package com.xxw.base.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;

/**
 * 作用在数据库实体的属性上
 * 实现程序中类型与数据库中类型的转化
 */

@Converter
public class Boolean2StringConverter implements AttributeConverter<Boolean, String>{

	
	public String convertToDatabaseColumn(Boolean attribute) {

		return attribute ? "Y" : "N";
	}

	public Boolean convertToEntityAttribute(String dbData) {
		return "Y".equals(dbData);
	}
}

//class A {
//	@Convert(converter = Boolean2StringConverter.class)
//	private boolean isValid;
//
//}
