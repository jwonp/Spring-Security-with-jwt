package com.ikiningyou.lecture.configuration;

import com.ikiningyou.lecture.service.UserService;
import com.ikiningyou.lecture.utils.JwtUtil;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final UserService userService;
  private final String secretKey;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    log.info("authorization : {}", authorization);

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      log.error("authorization 없습니다.");
      filterChain.doFilter(request, response);
      return;
    }

    //Token 꺼내기
    String token = authorization.split(" ")[1];

    if (JwtUtil.isTokenNotVaild(token, secretKey)) {
      log.error("Token is Not vaild");
      filterChain.doFilter(request, response);
      return;
    }

    //is Token Expired
    if (JwtUtil.isExpired(token, secretKey)) {
      log.error("Token is expired");
      filterChain.doFilter(request, response);
      return;
    }

    //username Token에서 꺼내기
    String username = JwtUtil.getUsername(token, secretKey);

    log.info("username:{}", username);

    //권한 부여
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
      username,
      null,
      List.of(new SimpleGrantedAuthority("USER"))
    );

    //Detail
    authenticationToken.setDetails(
      new WebAuthenticationDetailsSource().buildDetails(request)
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    filterChain.doFilter(request, response);
  }
}
