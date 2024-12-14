package cn.jongwong.server.config.security.jwt;

import cn.jongwong.server.dto.JwtUser;
import cn.jongwong.server.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret:ufPebxM1SjSjTNGzEQlQAkyhX0Nn0CHqSp0kO4Ia4EnLWv01nk3P+JmS6R8nxqEtAUWdoY+o0GP9z2dUc1qHxg==}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long expirationTime;  // 默认1小时（3600000毫秒）

    // 生成 JWT
    public String generateToken(User user) {
        List<String> authoritiesArray = List.of(user.getAuthorities());

        return Jwts.builder()
                .setSubject(user.getId())
                .claim("id", user.getId()) // 自定义 claim，存储用户 ID
                .claim("username", user.getUsername()) // 自定义 claim，存储用户名
                .claim("name", user.getName())
                .claim("nickname", user.getNickname())
                .claim("authorities", authoritiesArray)  // 使用 String[] 类型
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // 提取 JWT 中的用户信息
    public JwtUser extractUser(String token) {
        String _token = token.startsWith("Bearer ") ? token.substring(7) : token;

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(_token)
                .getBody();

        String userId = claims.get("id", String.class);
        String username = claims.get("username", String.class);
        String name = claims.get("name", String.class);
        String nickname = claims.get("nickname", String.class);

        // 提取并转换 authorities
        Object authoritiesObj = claims.get("authorities");
        String[] authorities = new String[0];
        if (authoritiesObj instanceof List) {
            authorities = ((List<String>) authoritiesObj).toArray(new String[0]);
        }

        JwtUser user = new JwtUser();
        user.setId(userId);
        user.setUsername(username);
        user.setName(name);
        user.setNickname(nickname);
        user.setStringAuthorities(authorities);

        return user;
    }

    // 提取 JWT 中的用户权限
    public String[] extractAuthorities(String token) {
        String _token = token.startsWith("Bearer ") ? token.substring(7) : token;

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(_token)
                .getBody();

        // 获取 authorities 并确保转换为 String[] 类型
        Object authoritiesObj = claims.get("authorities");
        if (authoritiesObj instanceof List) {
            List<String> authoritiesList = (List<String>) authoritiesObj;
            return authoritiesList.toArray(new String[0]);  // 转换为 String[]
        }
        return new String[0];  // 如果没有 authorities，返回空数组
    }

    public JwtUser validateToken(String token) {
        String _token = token.startsWith("Bearer ") ? token.substring(7) : token;

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 使用你的密钥
                .parseClaimsJws(_token)   // 解析 token
                .getBody();

        // 检查 token 是否已过期
        if (claims.getExpiration().before(new Date())) {
            throw new JwtException("Token has expired");
        }

        // 返回 JwtUser 对象
        return extractUser(token);

    }

    // 提取用户 ID
    public String extractUserId(String token) {
        String _token = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(_token)
                .getBody()
                .getSubject(); // 这里假设 `sub` 存储的是用户名
    }

    // 检查 token 是否过期
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 提取 token 的过期时间
    private Date extractExpiration(String token) {
        String _token = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(_token)
                .getBody()
                .getExpiration();
    }
}
