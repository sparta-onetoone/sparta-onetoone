package com.eureka.spartaonetoone.user.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.application.exception.UserException;
import com.eureka.spartaonetoone.user.application.UserService;
import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;
	private UUID userId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void testGetUserDetail() throws Exception {
		// 가짜 User 객체 생성
		User mockUser = mock(User.class);
		when(mockUser.getUsername()).thenReturn("testuser");
		when(mockUser.getEmail()).thenReturn("testuser@email.com");
		when(mockUser.getNickname()).thenReturn("nickname");
		when(mockUser.getPhoneNumber()).thenReturn("123456789");

		// 가짜 UserDetailResponseDto 생성
		UserDetailResponseDto mockResponseDto = mock(UserDetailResponseDto.class);
		when(mockResponseDto.getUsername()).thenReturn("testuser");
		when(mockResponseDto.getNickname()).thenReturn("nickname");
		when(mockResponseDto.getEmail()).thenReturn("testuser@email.com");

		// UserService의 getUserDetail 메소드가 가짜 ResponseDto를 반환하도록 설정
		when(userService.getUserDetail(userId)).thenReturn(mockResponseDto);

		// 테스트 실행 및 검증
		mockResponseDto = mock(UserDetailResponseDto.class);
		when(mockResponseDto.getUsername()).thenReturn("testuser");
		when(mockResponseDto.getEmail()).thenReturn("testuser@email.com");
		when(mockResponseDto.getNickname()).thenReturn("nickname");
		when(mockResponseDto.getPhoneNumber()).thenReturn("123456789");

		// UserService의 getUserDetail 메소드가 호출되었는지 확인
		verify(userService, times(1)).getUserDetail(userId);
	}
}