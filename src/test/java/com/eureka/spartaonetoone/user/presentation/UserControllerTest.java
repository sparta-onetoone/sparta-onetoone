// package com.eureka.spartaonetoone.user.presentation;
//
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.UUID;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.FilterType;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.eureka.spartaonetoone.common.config.SecurityConfig;
// import com.eureka.spartaonetoone.user.application.UserService;
// import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
// import com.eureka.spartaonetoone.user.domain.User;
// import com.eureka.spartaonetoone.user.domain.UserRole;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// @WebMvcTest(
// 	controllers = UserController.class,
// 	excludeFilters = {
// 		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
// 	}
// )
// class UserControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockitoBean
// 	private UserService userService;
//
// 	@DisplayName("유저 정보를 조회한다.")
// 	@Test
// 	void testGetUserDetail() throws Exception {
// 		// Given
// 		UUID userId = UUID.randomUUID();
// 		User user = new User(userId, "Test User", "test@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN);
//
// 		when(userService.getUserDetail(userId)).thenReturn(user);
//
// 		// When, Then
// 		mockMvc.perform(get("/api/v1/users/{user_id}", userId)
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data.userId").value(userId.toString()))
// 			.andExpect(jsonPath("$.data.username").value("Test User"))
// 			.andExpect(jsonPath("$.message").value("회원 정보 상세 조회 성공"));
//
// 		verify(userService).getUserDetail(userId);
// 	}
//
// 	@DisplayName("모든 유저 정보를 조회한다.")
// 	@Test
// 	void testGetAllUsers() throws Exception {
// 		// Given
// 		UUID userId = UUID.randomUUID();
// 		User user = new User(userId, "Test User", "test@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN);
//
// 		when(userService.getAllUsers()).thenReturn(List.of(user));
//
// 		// When, Then
// 		mockMvc.perform(get("/api/v1/users")
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data.content[0].userId").value(userId.toString()))
// 			.andExpect(jsonPath("$.data.content[0].username").value("Test User"))
// 			.andExpect(jsonPath("$.message").value("회원 정보 전체 조회 성공"));
//
// 		verify(userService).getAllUsers();
// 	}
//
// 	@DisplayName("유저 정보를 수정한다.")
// 	@Test
// 	void testUpdateUser() throws Exception {
// 		// Given
// 		UUID userId = UUID.randomUUID();
// 		UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("Updated User", "updated@example.com");
// 		User updatedUser = new User(userId, "Updated User", "updated@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN);
//
// 		when(userService.updateUser(userId, updateRequest)).thenReturn(updatedUser);
//
// 		// When, Then
// 		mockMvc.perform(put("/api/v1/users/{user_id}", userId)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(updateRequest)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data.username").value("Updated User"))
// 			.andExpect(jsonPath("$.data.email").value("updated@example.com"))
// 			.andExpect(jsonPath("$.message").value("회원 정보 수정 성공"));
//
// 		verify(userService).updateUser(userId, updateRequest);
// 	}
//
// 	@DisplayName("유저를 삭제한다.")
// 	@Test
// 	void testDeleteUser() throws Exception {
// 		// Given
// 		UUID userId = UUID.randomUUID();
//
// 		// When, Then
// 		mockMvc.perform(delete("/api/v1/users/{user_id}", userId)
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data.userId").value(userId.toString()))
// 			.andExpect(jsonPath("$.message").value("회원 탈퇴 성공"));
//
// 		verify(userService).deleteUser(userId);
// 	}
//
// 	@DisplayName("인증되지 않은 사용자 접근 시 401 오류가 발생한다.")
// 	@Test
// 	void testUnauthorizedAccess() throws Exception {
// 		// When, Then
// 		mockMvc.perform(get("/api/v1/users/{user_id}", UUID.randomUUID())
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isUnauthorized());
// 	}
// }
