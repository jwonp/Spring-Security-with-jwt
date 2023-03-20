package com.ikiningyou.lecture.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;

public class JwtUtil {

  // private static String EXPIRED_JWT_EXCEPTION = "EXPIRED_JWT_EXCEPTION";
  // private static String SIGNATURE_EXCEPTION = "SIGNATURE_EXCEPTION";
  // private static String MALFORMED_JWT_EXCEPTION = "MALFORMED_JWT_EXCEPTION";
  // private static String UNSUPPORTED_JWT_EXCEPTION = "UNSUPPORTED_JWT_EXCEPTION";

  public static boolean isTokenNotVaild(String token, String secretKey) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return false;
    } catch (ExpiredJwtException e) {
      return true;
    } catch (SignatureException e) {
      return true;
    } catch (MalformedJwtException e) {
      return true;
    } catch (UnsupportedJwtException e) {
      return true;
    }
  }

  public static String getUsername(String token, String secretKey) {
    return Jwts
      .parser()
      .setSigningKey(secretKey)
      .parseClaimsJws(token)
      .getBody()
      .get("username", String.class);
  }

  public static boolean isExpired(String token, String secretKey) {
    return Jwts
      .parser()
      .setSigningKey(secretKey)
      .parseClaimsJws(token)
      .getBody()
      .getExpiration()
      .before(new Date());
  }

  public static String createToken(
    String username,
    String key,
    long expireTimeMs
  ) {
    Claims claims = Jwts.claims();
    claims.put("username", username);

    return Jwts
      .builder()
      .setClaims(claims)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
      .signWith(SignatureAlgorithm.HS256, key)
      .compact();
  }
}
