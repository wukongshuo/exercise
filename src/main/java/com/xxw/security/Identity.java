package com.xxw.security;

import com.xxw.exception.BusinessException;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 登陆之后
 * <p>
 * 1. 乘客端的微信小程序登陆
 * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
 * 2. 司机端和后台管理人员的登陆用 OAuthen 2.0
 *
 * @author chenmd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Identity implements Authentication {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userType;
    private String userId;
    private List<String> userRoles;

    private boolean isAuthenticated;
    private String password;
    private String openId;

    public JwtToken generateJwtToken(String secret) {
        return this.generateJwtToken(secret, 5 * 60 * 60);
    }

    public JwtToken generateJwtToken(String secret, Date exparationDate) {
        String token = new JwtTokenTool(secret).generateToken(getClaims(), exparationDate);
        return new JwtToken(token);
    }

    public JwtToken generateJwtToken(String secret, long validity) {

        String token = new JwtTokenTool(secret, validity).generateToken(getClaims());
        return new JwtToken(token);
    }


    private Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", getUserId());
        claims.put("userRoles", getUserRoles());
        return claims;
    }


    public static Identity buildFromJwtToken(String token, String secret) {
        JwtTokenTool tool = new JwtTokenTool(secret);
        Claims allClaims = tool.getAllClaimsFromToken(token);
        Boolean expired = tool.isTokenExpired(token);
        if (expired) {
            throw BusinessException.error("iam-4001", "token 已经过期！");
        }

        return Identity.builder()
                .userId(allClaims.get("userId", String.class))
                .userType(allClaims.get("userType", String.class))
                .userRoles(allClaims.get("userRoles", List.class))
                .openId(allClaims.get("openId", String.class))
                .isAuthenticated(true)
                .build();
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRoles != null && !userRoles.isEmpty()) {
            return userRoles.stream().map(x -> {
                return new SimpleGrantedAuthority(x);
            }).collect(Collectors.toList());
        }
        return null;
    }


    public Object getCredentials() {
        return password;
    }


    public Object getDetails() {
        // TODO Auto-generated method stub
        return null;
    }


    public String getPrincipal() {
        return this.userId;
    }


    public boolean isAuthenticated() {
        return isAuthenticated;
    }


    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return this.userId;
    }
}