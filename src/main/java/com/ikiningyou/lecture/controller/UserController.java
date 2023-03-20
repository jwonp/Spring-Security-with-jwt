package com.ikiningyou.lecture.controller;

// import com.ikiningyou.lecture.domain.User;
import com.ikiningyou.lecture.domain.dto.UserJoinRequest;
import com.ikiningyou.lecture.domain.dto.UserLoginRequest;
import com.ikiningyou.lecture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService useService;

  @PostMapping("/join")
  public ResponseEntity<String> join(@RequestBody UserJoinRequest dto) {
    useService.join(dto.getUsername(), dto.getPassword());
    return ResponseEntity.ok().body("회원가입 성공");
  }

  @PostMapping("/login")
  public ResponseEntity<String> log(@RequestBody UserLoginRequest dto) {
    String token = useService.login(dto.getUsername(), dto.getPassword());
    return ResponseEntity.ok().body(token);
  }
}
