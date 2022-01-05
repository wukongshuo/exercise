package com.xxw.security;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//@ConfigurationProperties(prefix = SecurityProperties.SECURITY_PREFIX)
@Component
public class SecurityProperties {
	public static final String SECURITY_PREFIX = "com/xxw/security";

	private String[] pathPatterns;
	
	private String[] excludePathPatterns;

	private String secret;
}
