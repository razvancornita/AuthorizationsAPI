package com.authorization.service;

import com.authorization.exception.UnauthorizedException;
import com.authorization.mongo.entity.LoginEntity;
import com.authorization.repository.LoginRepository;
import com.authorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userEntity = userRepository.findByName(username).orElseThrow(NoSuchElementException::new);
        return User.withUsername(userEntity.getName())
                .password(passwordEncoder.encode(String.valueOf(userEntity.getPin())))
                .authorities("user").build();
    }

    public void checkIfLoginIsValid(String username) {
        Optional<LoginEntity> login = loginRepository.findByName(username);
        if (login.isPresent() && !login.get().getLoggedIn()) {
            throw new UnauthorizedException("user previously logged out");
        }
    }
}