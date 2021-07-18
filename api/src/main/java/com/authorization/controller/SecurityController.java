package com.authorization.controller;

import com.authorization.security.JwtTokenProvider;
import com.authorization.security.SecurityUtility;
import com.authorization.service.DatabaseService;
import com.authorization.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
public class SecurityController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final DatabaseService databaseService;
    private final SecurityUtility securityUtility;


    public SecurityController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                              DatabaseService databaseService, SecurityUtility securityUtility) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.databaseService = databaseService;
        this.securityUtility = securityUtility;
    }


    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPin()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        databaseService.saveLogin(user.getUserName());
        return ResponseEntity.ok("{ \"Bearer\" : \"" + tokenProvider.generateToken(authentication) + "\"}");
    }

    @DeleteMapping("/authorization/logout")
    public ResponseEntity<String> logout() {
        String userName = securityUtility.getUserName();
        databaseService.invalidateLogin(userName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User logged out");
    }
}
