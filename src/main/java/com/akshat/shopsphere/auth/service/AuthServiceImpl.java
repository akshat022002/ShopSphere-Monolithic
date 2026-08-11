package com.akshat.shopsphere.auth.service;

import com.akshat.shopsphere.auth.dto.AuthResponse;
import com.akshat.shopsphere.auth.dto.LoginRequest;
import com.akshat.shopsphere.auth.dto.RefreshTokenRequest;
import com.akshat.shopsphere.auth.dto.RegisterRequest;
import com.akshat.shopsphere.auth.mapper.UserMapper;
import com.akshat.shopsphere.common.exception.BadRequestException;
import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.security.jwt.JwtService;
import com.akshat.shopsphere.security.redis.TokenBlacklistService;
import com.akshat.shopsphere.security.service.CustomUserDetailsService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import com.akshat.shopsphere.user.entity.AuthProvider;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        log.info("Register request received for email : {}", request.email());

      if(userRepository.existsByEmail(request.email())){
          throw new DuplicateResourceException("Email already registered");
      }

      User user = User.builder()
              .name(request.name())
              .email(request.email())
              .password(passwordEncoder.encode(request.password()))
              .role(request.role())
              .authProvider(AuthProvider.LOCAL)
              .enabled(true)
              .build();

      userRepository.save(user);

      String accessToken = jwtService
        .generateAccessToken(new
                UserDetailsImplementation(user));

      String refreshToken = jwtService
              .generateRefreshToken(new
                   UserDetailsImplementation(user));

        log.info("User registered successfully : {}", user.getEmail());

      return new AuthResponse(accessToken,refreshToken);

    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Login request received for : {}",request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        String accessToken = jwtService.generateAccessToken(userDetails);

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("User logged in successfully : {}", request.email());

        return new AuthResponse(accessToken,refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.refreshToken();

        String email = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if(!jwtService.isTokenValid(refreshToken,userDetails)){
            throw new BadRequestException("Invalid/Expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(newAccessToken,newRefreshToken);
    }

    @Override
    public void logout(String accessToken) {

        log.info("Logout request received");
        tokenBlacklistService.BlacklistToken(accessToken);
        log.info("Token blacklisted successfully");

    }
}
