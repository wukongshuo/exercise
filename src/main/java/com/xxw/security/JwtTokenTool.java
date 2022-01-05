package com.xxw.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

class JwtTokenTool implements Serializable {
	private static final long serialVersionUID = -2550185165626007488L;

	public  long JWT_TOKEN_VALIDITY = 15 * 60;
	

	@Value("${jwt.secret}")
	private String secret;
	
	public JwtTokenTool() {
		
	}
	
	public JwtTokenTool(String secret, long validity) {
		this.secret = secret;
		this.JWT_TOKEN_VALIDITY = validity;
	}
	
	public JwtTokenTool(String secret) {
		this.secret = secret;
	}



	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}


	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	public String generateToken(Map<String, Object> claims) {
		return generateToken(claims, new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000));
	}
	
	public String generateToken(Map<String, Object> claims, Date expirationDate) {
		return Jwts.builder().setClaims(claims)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
}
