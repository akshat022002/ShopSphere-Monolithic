package com.akshat.shopsphere.security.oauth2;

import com.akshat.shopsphere.user.entity.AuthProvider;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.enums.Role;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        if (!"google".equalsIgnoreCase(registrationId)) {
            throw new OAuth2AuthenticationException(
                    "Unsupported OAuth provider"
            );
        }

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    "Email not provided by Google"
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createOAuthUser(email, name));

        log.info(
                "OAuth2 user authenticated: email={}, provider={}",
                email,
                registrationId
        );

        return oidcUser;
    }

    private User createOAuthUser(String email, String name) {

        User user = User.builder()
                .name(name)
                .email(email)
                .password(null)
                .role(Role.ROLE_CUSTOMER)
                .enabled(true)
                .authProvider(AuthProvider.GOOGLE)
                .build();

        User savedUser = userRepository.save(user);

        log.info(
                "New Google user created: email={}",
                savedUser.getEmail()
        );

        return savedUser;
    }
}

