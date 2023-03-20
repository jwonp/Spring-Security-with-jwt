package com.ikiningyou.lecture.configuration;

import com.ikiningyou.lecture.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig {

  @Autowired
  private UserService userService;

  @Value("${jwt.token.secret}")
  private String secretKey;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    throws Exception {
    return httpSecurity
      .httpBasic(c -> c.disable())
      .csrf(c -> c.disable())
      .cors(c -> {})
      .authorizeHttpRequests(req ->
        req
          .antMatchers("/api/v1/users/login", "/api/v1/users/join")
          .permitAll()
          .antMatchers(HttpMethod.POST, "/api/v1/reviews")
          .authenticated()
      )
      .sessionManagement(c ->
        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .addFilterBefore(
        new JwtFilter(userService, secretKey),
        UsernamePasswordAuthenticationFilter.class
      )
      .build();
  }
}
