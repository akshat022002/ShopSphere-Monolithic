package com.akshat.shopsphere.security.service;

import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(()-> new
                        ResourceNotFoundException("User not found with Email: "+ email));

        return new UserDetailsImplementation(user);

    }
}
