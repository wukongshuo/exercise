package com.xxw.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	//@Autowired
	private AuthenticationEntryPoint authEntryPoint;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
				.anyRequest().authenticated()
				.and().httpBasic()
				.authenticationEntryPoint(authEntryPoint);
	}

	//@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
	}
	
	//@Component
	public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
		@Override
	   public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException {
	      response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
	   }	
	}
		
}
