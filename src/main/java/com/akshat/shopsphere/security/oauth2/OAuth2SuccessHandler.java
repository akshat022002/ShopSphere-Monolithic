package com.akshat.shopsphere.security.oauth2;

import com.akshat.shopsphere.security.jwt.JwtService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {


        OAuth2User oAuth2User =
                (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        log.info("OAuth2SuccessHandler email = {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->

                     new IllegalStateException(
                            "OAuth2 user not found"));


        UserDetailsImplementation userDetails =
                new UserDetailsImplementation(user);

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        log.info(
                "Google login successful for user: {}",
                email
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                    "success": true,
                    "message": "Google login successful",
                    "data": {
                        "accessToken": "%s",
                        "refreshToken": "%s"
                    }
                }
                """.formatted(accessToken, refreshToken));
    }
}