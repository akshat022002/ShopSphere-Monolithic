package com.akshat.shopsphere.security;

import com.akshat.shopsphere.security.jwt.JwtAuthenticationFilter;
import com.akshat.shopsphere.security.oauth2.CustomOAuth2UserService;
import com.akshat.shopsphere.security.oauth2.OAuth2FailureHandler;
import com.akshat.shopsphere.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSec){

        httpSec

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors->{})

                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/auth/**","/swagger-ui/**","/v3/api-docs/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.POST,"/api/categories","/api/categories/*/subcategories").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,"/api/categories/*","/api/categories/subcategories/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,"/api/categories/*","/api/categories/subcategories/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,"/api/categories/**").authenticated()

                        .anyRequest()
                        .authenticated())

                .authenticationProvider(authenticationProvider)

                .oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfo ->
                                        userInfo.oidcUserService(oAuth2UserService)
                                )
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2FailureHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

      return  httpSec.build();

    }

}
