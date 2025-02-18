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

    // 회원가입 로직
    @Transactional
    public AuthSignupResponseDto signup(AuthSignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException.DuplicateEmail(); // 이메일 중복 예외 처리
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .nickname("nodaji")
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.of(request.getRole())) // 문자열 → Enum 변환
                .isDeleted(false)
                .phoneNumber("010101010101")
                .grade(UserGrade.SILVER)
                .addresses(new ArrayList<>()) // 초기 주소 리스트
                .build();


        // 주소 생성
        UserAddress address = UserAddress.builder()
                .city("오")
                .district("어")
                .roadName("어")
                .zipCode("2323124")
                .isDeleted(false)
                .build();

        address.setUser(user);  // 주소에 사용자 설정

        user.getAddresses().add(address); // 사용자에 주소 추가

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