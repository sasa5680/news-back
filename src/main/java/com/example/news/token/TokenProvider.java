package com.example.news.token;

import com.example.news.exception.ErrorCode;
import com.example.news.exception.NoAuthException;
import com.example.news.types.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider implements InitializingBean {

    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {

        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);

    }

    //유저 role과 id 이용해서 jwt 토큰 생성
    public String createToken(UserRole userRole , int userId) {

        //yml 파일에서 설정한 시간을 이용해 토큰 생성
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                //.setSubject(email)

                .claim("userRole", userRole.getUserRoleType())
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public int getUserIdFromRequest(HttpServletRequest httpServletRequest){
        String bearerToken = httpServletRequest.getHeader("Authorization");
        String token = getTokenFromBearer(bearerToken);
        Jws<Claims> claims =  parseToken(token);

        return ((Integer) claims.getBody().get("userId"));
    }

    public String getRoleFromRequest(HttpServletRequest request){

        String bearerToken = request.getHeader("Authorization");
        String token = getTokenFromBearer(bearerToken);
        return getUserRoleFromToken(token);
    }

    //토큰에서 유저 권한정보 추출
    public String getUserRoleFromToken(String token){
        //결과값 = Claims
        Jws<Claims> claims = parseToken(token);
        return ((String) claims.getBody().get("userRole"));
    }

    public int getUserIdFromToken(String token) {
        Jws<Claims> claims = parseToken(token);
        return ((Integer) claims.getBody().get("userId"));
    }

    private String getTokenFromBearer(String bearer) {

        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    //토큰의 유효성 검사
    private Jws<Claims> parseToken(String token) {

        //토큰이 없는 경우
        if(token == null) throw new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR);

        //파싱 후 문제가 발생하면 예외 발생시킴
        //비밀키를 이용해서 복호화 하는 작업
        //jwt가 유효한지, 위변조 되지 않았는지 판단한다.
        //이 비밀키는 서버에만 존재해야되고, 유출되어서는 안된다.
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return claims;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new NoAuthException(ErrorCode.TOKEN_MAL_ERROR);
        } catch (ExpiredJwtException e) {
            throw new NoAuthException(ErrorCode.TOKEN_EXPIRATION_ERROR);
        } catch (UnsupportedJwtException e) {
            throw new NoAuthException(ErrorCode.TOKEN_MAL_ERROR);
        } catch (IllegalArgumentException e) {
            throw new NoAuthException(ErrorCode.TOKEN_MAL_ERROR);
        }
    }
}
