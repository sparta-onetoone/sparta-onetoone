package com.eureka.spartaonetoone.useraddress.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.eureka.spartaonetoone.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.auth.config.JwtSecurityFilter;
import com.eureka.spartaonetoone.common.config.SecurityConfig;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsServiceImpl;
import com.eureka.spartaonetoone.useraddress.application.UserAddressService;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.response.UserAddressResponseDto;
import com.eureka.spartaonetoone.useraddress.domain.repository.UserAddressRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserAddressController.class)
@Import({SecurityConfig.class, JwtUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserAddressControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserAddressService userAddressService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserAddressRepository userAddressRepository;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean // 추가!
	private UserDetailsServiceImpl userDetailsService;
	//
	@MockitoBean
	private JwtSecurityFilter jwtSecurityFilter;

	private final UUID testUserId = UUID.randomUUID();
	private final UUID testAddressId = UUID.randomUUID();

	@Test
	@DisplayName("주소를 정상적으로 추가할 수 있다.")
	@WithMockUser(username = "81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9", roles = "ADMIN")
	void addAddress_success() throws Exception {
		// [1] 요청 데이터 생성
		UserAddressRequestDto request = new UserAddressRequestDto(
			"서울1", "강남구", "테헤란로 123", "12345", "아파트 101호"
		);

		UUID testUserId = UUID.fromString("81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9"); // 테스트용 ID

		// [2] 모킹 데이터 생성
		UserAddressResponseDto responseDto = UserAddressResponseDto.builder()
			.addressId(testAddressId)
			.city("서울")
			.district("강남구")
			.roadName("테헤란로 123")
			.zipCode("12345")
			.detail("아파트 101호")
			.build();

		// [3] 서비스 모킹
		when(userAddressService.addAddress(any(), any()))
			.thenReturn(responseDto);

		// [4] 테스트 실행
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/addresses/{user_id}", testUserId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print()) // 응답 출력
			.andExpect(status().isOk())
			// .andExpect(jsonPath("$.data.city").value("서울"))
			.andExpect(jsonPath("$.message").value("주소 추가 성공"))
			.andExpect(jsonPath("$.data.addressId").value(testAddressId.toString()))
			.andExpect(jsonPath("$.data.city").value("서울"))
			.andExpect(jsonPath("$.data.district").value("강남구"))
			.andExpect(jsonPath("$.data.roadName").value("테헤란로 123"))
			.andExpect(jsonPath("$.data.zipCode").value("12345"))
			.andExpect(jsonPath("$.data.detail").value("아파트 101호"));

		verify(userAddressService, times(1)).addAddress(any(), any());

	}
	@Test
	@DisplayName("주소를 정상적으로 삭제할 수 있다.")
	@WithMockUser(username = "81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9", roles = "ADMIN")
	void deleteAddress_success() throws Exception {
		// [1] 모킹 데이터 생성
		UserAddressResponseDto responseDto = new UserAddressResponseDto(testAddressId, "서울", "강남구", "테헤란로 123", "12345", "아파트 101호");

		// [2] 서비스 모킹
		when(userAddressService.deleteAddress(testAddressId)).thenReturn(responseDto);

		// [3] 테스트 실행
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/addresses/{address_id}", testAddressId)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print()) // 응답 출력
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("주소 삭제 성공"))
			.andExpect(jsonPath("$.data.addressId").value(testAddressId.toString()))
			.andExpect(jsonPath("$.data.city").value("서울"))
			.andExpect(jsonPath("$.data.district").value("강남구"))
			.andExpect(jsonPath("$.data.roadName").value("테헤란로 123"))
			.andExpect(jsonPath("$.data.zipCode").value("12345"))
			.andExpect(jsonPath("$.data.detail").value("아파트 101호"));

		verify(userAddressService, times(1)).deleteAddress(testAddressId);
	}
	@Test
	@DisplayName("주소를 정상적으로 수정할 수 있다.")
	@WithMockUser(username = "81b949b2-0cc9-4bb9-b6f8-31c7bf4d6ab9", roles = "ADMIN")
	void updateAddress_success() throws Exception {
		// [1] 요청 데이터 생성
		UserAddressRequestDto request = new UserAddressRequestDto(
			"서울", "강남구", "테헤란로 123", "12345", "아파트 101호"
		);

		UUID testAddressId = UUID.randomUUID(); // 테스트용 주소 ID

		// [2] 모킹 데이터 생성
		UserAddressResponseDto responseDto = UserAddressResponseDto.builder()
			.addressId(testAddressId)
			.city("서울")
			.district("강남구")
			.roadName("테헤란로 123")
			.zipCode("12345")
			.detail("아파트 101호")
			.build();

		// [3] 서비스 모킹
		when(userAddressService.updateAddress(eq(testAddressId), any()))
			.thenReturn(responseDto);

		// [4] 테스트 실행
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/addresses/{address_id}", testAddressId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print()) // 응답 출력
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("주소 수정 성공"))
			.andExpect(jsonPath("$.data.addressId").value(testAddressId.toString()))
			.andExpect(jsonPath("$.data.city").value("서울"))
			.andExpect(jsonPath("$.data.district").value("강남구"))
			.andExpect(jsonPath("$.data.roadName").value("테헤란로 123"))
			.andExpect(jsonPath("$.data.zipCode").value("12345"))
			.andExpect(jsonPath("$.data.detail").value("아파트 101호"));

		verify(userAddressService, times(1)).updateAddress(eq(testAddressId), any());
	}

}