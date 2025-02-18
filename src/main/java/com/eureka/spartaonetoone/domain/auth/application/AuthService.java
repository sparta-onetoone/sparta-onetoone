package com.eureka.spartaonetoone.domain.auth.application;

import com.eureka.spartaonetoone.domain.address.domain.UserAddress;
import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSignoutRequestDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.response.AuthSignupResponseDto;
import com.eureka.spartaonetoone.domain.auth.application.exception.AuthException;
import com.eureka.spartaonetoone.domain.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.domain.user.domain.User;
import com.eureka.spartaonetoone.domain.user.domain.UserGrade;
import com.eureka.spartaonetoone.domain.user.domain.UserRole;
import com.eureka.spartaonetoone.domain.user.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //회원가입
    @Transactional
    public AuthSignupResponseDto signup(AuthSignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException.DuplicateEmail(); // 이메일 중복 예외 처리
        }

        // User 엔티티 생성
        // 사용자 생성
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .grade(UserGrade.SILVER)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 주소 생성 및 연결
        UserAddress address = UserAddress.builder()
                .addressId(UUID.randomUUID())
                .city(request.getCity())
                .district(request.getDistrict())
                .roadName(request.getRoadName())
                .zipCode(request.getZipCode())
                .detail(request.getDetail())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user.addAddress(address); // 사용자와 주소 연결

        // 사용자 저장 (주소도 함께 저장됨)
        userRepository.save(user);

        return AuthSignupResponseDto.from(user);
    }

    // 로그인 로직
    public AuthSigninResponseDto signin(AuthSigninRequestDto request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(AuthException.UserNotFound::new);

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException.InvalidPassword();
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        // 리프레시 토큰 저장
        user.updateRefreshToken(refreshToken);

        return AuthSigninResponseDto.of(accessToken, refreshToken);
    }
    //로그아웃
    @Transactional
    public void signout(String email, String token) {
        // 이메일을 사용해 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthException.UserNotFound::new);

        // JWT 토큰 검증
        String jwtToken = token.replace("Bearer ", "");
        if (!jwtUtil.validateToken(jwtToken)) {
            throw new AuthException.InvalidTokenException();
        }

        // 리프레시 토큰 무효화
        user.updateRefreshToken(null);
    }
}