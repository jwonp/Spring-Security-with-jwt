package com.ikiningyou.lecture.service;

import com.ikiningyou.lecture.domain.User;
import com.ikiningyou.lecture.exception.AppException;
import com.ikiningyou.lecture.exception.ErrorCode;
import com.ikiningyou.lecture.repository.UserRepository;
import com.ikiningyou.lecture.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Value("${jwt.token.secret}")
  private String key;

  private Long expireTimeMs = 1000 * 5l;

  public String join(String username, String password) {
    //userName 중복 check
    userRepository
      .findByUsername(username)
      .ifPresent(user -> {
        throw new AppException(
          ErrorCode.USERNAME_DUPLICATED,
          username + "는 이미 있습니다."
        );
      });

    // 저장
    User user = User
      .builder()
      .username(username)
      .password(bCryptPasswordEncoder.encode(password))
      .build();

    userRepository.save(user);
    return "SUCCESS";
  }

  public String login(String username, String password) {
    // no username
    User selectedUser = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new AppException(ErrorCode.USERNAME_NOT_FOUND, "이 없습니다.")
      );
    // invaild password
    if (!bCryptPasswordEncoder.matches(password, selectedUser.getPassword())) {
      throw new AppException(
        ErrorCode.INVAILD_PASSWORD,
        "패스워드를 잘못 입력 했습니다."
      );
    }

    String token = JwtUtil.createToken(
      selectedUser.getUsername(),
      key,
      expireTimeMs
    );

    //앞에서 Exception 안 났으면 토큰 발행
    return token;
  }
}
