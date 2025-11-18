package graduation.project.DoDutch_server.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.GeneralException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Random;

@Slf4j
@Component
@Service
public class JwtTokenProvider implements InitializingBean {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;            // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final String secretKey; // Jwt 시크릿 키
    private static Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    /*
     * 비밀키를 Base64 인코딩하고 다시 키로 변환하여 저장
     */
    @Override
    public void afterPropertiesSet() {
        this.key = getKeyFromBase64EncodedKey(encodeBase64SecretKey(secretKey));
    }

    //여기서 payload는 멤버의 socialId
    public String createAccessToken(String payload) {
        return createToken(payload, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        return createToken(generatedString, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createToken(String payload, long expireLength) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * 페이로드 추출
     */
    public String getPayload(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
    }

    // 토큰의 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }
}
