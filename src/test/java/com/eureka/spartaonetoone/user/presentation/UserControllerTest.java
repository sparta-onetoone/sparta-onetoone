package com.eureka.spartaonetoone.user.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.eureka.spartaonetoone.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.common.config.SecurityConfig;
import com.eureka.spartaonetoone.user.application.UserService;
import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtUtil.class})
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserDetailsServiceImpl userDetailsService;

	@MockitoBean
	private UserRepository userRepository;

	private final UUID testUserId = UUID.randomUUID();

	@Test
	void 회원_단건_조회_성공() throws Exception {
		// Given
		UUID testUserId = UUID.fromString("81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9"); // 테스트용 ID
		UserDetailResponseDto response = new UserDetailResponseDto(
			testUserId, "user1", "nick", "user1@test.com", "010-1111-1111"
		);

		// 인증된 사용자 정보 설정 (Reflection으로 userId 설정)
		User user = User.create("user1", "user1@test.com", "password", "nick1", "010-1111-1111", UserRole.ADMIN);

		// 리플렉션을 사용하여 userId 값을 강제로 설정
		Field userIdField = User.class.getDeclaredField("userId");
		userIdField.setAccessible(true);
		userIdField.set(user, testUserId);

		UserDetailsImpl userDetails = new UserDetailsImpl(user); // 실제 UserDetails 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// mock userService
		when(userService.getUserDetail(testUserId)).thenReturn(response);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{user_id}", testUserId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userId").value(testUserId.toString()))
			.andExpect(jsonPath("$.data.username").value("user1"))
			.andExpect(jsonPath("$.data.email").value("user1@test.com"))
			.andExpect(jsonPath("$.message").value("회원 정보 상세 조회 성공"));

		verify(userService, times(1)).getUserDetail(testUserId);
	}
	@Test
	void 회원_다건_조회_성공() throws Exception {
		// Given
		User user1 = User.create("user1", "user1@test.com", "password", "nick1", "010-1111-1111", UserRole.ADMIN);
		User user2 = User.create("user2", "user2@test.com", "password", "nick2", "010-2222-2222", UserRole.OWNER);

		// ID 강제로 설정
		UUID testUserId1 = UUID.fromString("81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9");
		UUID testUserId2 = UUID.fromString("91b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9");

		Field userIdField1 = User.class.getDeclaredField("userId");
		userIdField1.setAccessible(true);
		userIdField1.set(user1, testUserId1);

		Field userIdField2 = User.class.getDeclaredField("userId");
		userIdField2.setAccessible(true);
		userIdField2.set(user2, testUserId2);

		// 응답 데이터 준비
		List<UserListResponseDto> responseList = List.of(
			new UserListResponseDto(user1.getUserId(), user1.getUsername(), user1.getEmail()),
			new UserListResponseDto(user2.getUserId(), user2.getUsername(), user2.getEmail())
		);

		// mock userService
		when(userService.getAllUsers(any(Pageable.class))).thenReturn(new PageImpl<>(responseList));

		// 인증된 사용자 정보 설정 (Reflection으로 userId 설정)
		UserDetailsImpl userDetails = new UserDetailsImpl(user1); // 인증할 사용자 설정
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 설정

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())  // 200 OK
			.andExpect(jsonPath("$.message").value("회원 정보 전체 조회 성공"))
			.andExpect(jsonPath("$.data.content[0].userId").value(testUserId1.toString())) // 수정된 부분
			.andExpect(jsonPath("$.data.content[0].username").value(user1.getUsername()))
			.andExpect(jsonPath("$.data.content[0].email").value(user1.getEmail()))
			.andExpect(jsonPath("$.data.content[1].userId").value(testUserId2.toString())) // 수정된 부분
			.andExpect(jsonPath("$.data.content[1].username").value(user2.getUsername()))
			.andExpect(jsonPath("$.data.content[1].email").value(user2.getEmail()));

		verify(userService, times(1)).getAllUsers(any(Pageable.class));
	}
	@Test
	void 회원_수정_성공() throws Exception {
		// Given
		UUID testUserId = UUID.fromString("81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9"); // 테스트용 ID
		UserUpdateRequestDto request = new UserUpdateRequestDto("newUsername", "newEmail@example.com"); // 수정할 정보

		// 응답 데이터 준비
		UserUpdateResponseDto response = new UserUpdateResponseDto(
			testUserId, "newUsername", "newEmail@example.com"
		);

		// userService mock 설정 (eq(request) → any(UserUpdateRequestDto.class)로 변경)
		when(userService.updateUser(eq(testUserId), any(UserUpdateRequestDto.class)))
			.thenReturn(response);

		// 인증된 사용자 정보 설정 (Reflection으로 userId 설정)
		User user = User.create("user1", "user1@test.com", "password", "nick1", "010-1111-1111", UserRole.ADMIN);

		// 리플렉션을 사용하여 userId 값을 강제로 설정
		Field userIdField = User.class.getDeclaredField("userId");
		userIdField.setAccessible(true);
		userIdField.set(user, testUserId);

		UserDetailsImpl userDetails = new UserDetailsImpl(user); // 실제 UserDetails 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/{user_id}", testUserId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))) // 요청 바디
			.andDo(print())  // 디버깅을 위한 응답 출력
			.andExpect(status().isOk())  // 200 OK
			.andExpect(jsonPath("$.message").value("회원 정보 수정 성공"))
			.andExpect(jsonPath("$.data.userId").value(testUserId.toString()))
			.andExpect(jsonPath("$.data.username").value("newUsername"))
			.andExpect(jsonPath("$.data.email").value("newEmail@example.com"));

		// Mock이 제대로 호출되었는지 검증
		verify(userService, times(1)).updateUser(eq(testUserId), any(UserUpdateRequestDto.class));
	}

	@Test
	void 회원_탈퇴_성공() throws Exception {
		// Given
		UUID testUserId = UUID.fromString("81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9"); // 테스트용 ID

		// 응답 데이터 준비
		UserDeleteResponseDto response = new UserDeleteResponseDto(testUserId);

		// Mock 설정
		when(userService.deleteUser(eq(testUserId))).thenReturn(response);

		// 인증된 사용자 정보 설정 (Reflection으로 userId 설정)
		User user = User.create("user1", "user1@test.com", "password", "nick1", "010-1111-1111", UserRole.ADMIN);

		// 리플렉션을 사용하여 userId 값을 강제로 설정
		Field userIdField = User.class.getDeclaredField("userId");
		userIdField.setAccessible(true);
		userIdField.set(user, testUserId);

		UserDetailsImpl userDetails = new UserDetailsImpl(user); // 실제 UserDetails 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{user_id}", testUserId)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())  // 응답을 콘솔에 출력
			.andExpect(status().isOk())  // 200 OK
			.andExpect(jsonPath("$.message").value("회원 탈퇴 성공"))
			.andExpect(jsonPath("$.data.userId").value(testUserId.toString()));

		// Mock이 제대로 호출되었는지 검증
		verify(userService, times(1)).deleteUser(eq(testUserId));
	}
}

