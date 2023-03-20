package com.ikiningyou.lecture.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikiningyou.lecture.domain.dto.UserJoinRequest;
import com.ikiningyou.lecture.domain.dto.UserLoginRequest;
import com.ikiningyou.lecture.exception.AppException;
import com.ikiningyou.lecture.exception.ErrorCode;
import com.ikiningyou.lecture.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  UserService userService;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  @DisplayName("회원가입 성공")
  @WithMockUser
  void join() throws Exception {
    String username = "kyengrok";
    String password = "qwerty";
    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/v1/users/join")
          .with(SecurityMockMvcRequestPostProcessors.csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            objectMapper.writeValueAsBytes(
              new UserJoinRequest(username, password)
            )
          )
      )
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("회원가입 실패 - userName 중복")
  @WithMockUser
  void join_fail() throws Exception {
    String username = "kyengrok";
    String password = "qwerty";

    when(userService.join(any(), any()))
      .thenThrow(new RuntimeException("해당 username이 중복됩니다."));

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/v1/users/join")
          .with(SecurityMockMvcRequestPostProcessors.csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            objectMapper.writeValueAsBytes(
              new UserJoinRequest(username, password)
            )
          )
      )
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  @DisplayName("로그인 성공")
  @WithMockUser
  void login_success() throws Exception {
    String username = "kyengrok";
    String password = "qwerty";

    when(userService.login(any(), any())).thenReturn("token");

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/v1/users/login")
          .with(SecurityMockMvcRequestPostProcessors.csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            objectMapper.writeValueAsBytes(
              new UserLoginRequest(username, password)
            )
          )
      )
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("로그인 실패 - username 없음")
  @WithMockUser
  void login_fail1() throws Exception {
    String username = "kyengrok";
    String password = "qwerty";

    when(userService.login(any(), any()))
      .thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/v1/users/login")
          .with(SecurityMockMvcRequestPostProcessors.csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            objectMapper.writeValueAsBytes(
              new UserLoginRequest(username, password)
            )
          )
      )
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("로그인 실패 - password 틀림")
  @WithMockUser
  void login_fail2() throws Exception {
    String username = "kyengrok";
    String password = "qwerty";

    when(userService.login(any(), any()))
      .thenThrow(new AppException(ErrorCode.INVAILD_PASSWORD, ""));

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/v1/users/login")
          .with(SecurityMockMvcRequestPostProcessors.csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            objectMapper.writeValueAsBytes(
              new UserLoginRequest(username, password)
            )
          )
      )
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }
}
