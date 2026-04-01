package com.youyinda.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.youyinda.util.HttpContextUtil;
import com.youyinda.util.SpringContextUtil;

/**
 * JWT工具类
 * 用于Token生成、校验、解析等操作
 */
@Component
@Slf4j
public class JwtUtil {

    /**
     * 密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成Token
     * @param claims 自定义声明
     * @return Token字符串
     */
    public String generateToken(Map<String, Object> claims) {
        try {
            Date now = new Date();
            Date expireDate = new Date(now.getTime() + expiration);

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (Exception e) {
            log.error("生成Token失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成Token
     * @param userId 用户ID
     * @param openid 微信OpenID
     * @return Token字符串
     */
    public String generateToken(Long userId, String openid) {
        try {
            Date now = new Date();
            Date expireDate = new Date(now.getTime() + expiration);

            return Jwts.builder()
                    .claim("userId", userId)
                    .claim("openid", openid)
                    .setIssuedAt(now)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (Exception e) {
            log.error("生成Token失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析Token
     * @param token Token字符串
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     * @param token Token字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("userId", Long.class);
        }
        return null;
    }

    /**
     * 从Token中获取OpenID
     * @param token Token字符串
     * @return OpenID
     */
    public String getOpenidFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("openid", String.class);
        }
        return null;
    }

    /**
     * 验证Token是否有效
     * @param token Token字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return false;
            }
            // 检查Token是否过期
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("验证Token失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取Token过期时间
     * @param token Token字符串
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.getExpiration();
        }
        return null;
    }

    /**
     * 从当前请求中获取用户ID
     * @return 用户ID
     */
    public static Long getUserIdFromToken() {
        HttpServletRequest request = HttpContextUtil.getRequest();
        if (request == null) {
            return null;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        JwtUtil jwtUtil = SpringContextUtil.getBean(JwtUtil.class);
        return jwtUtil.getUserIdFromToken(token);
    }
}
