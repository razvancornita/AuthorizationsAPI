package com.authorization.controller;

import com.authorization.authorizations.PowerOfAttorney;
import com.authorization.mongo.entity.UserEntity;
import com.authorization.service.AuthorizationService;
import com.authorization.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
@Slf4j
public class UnauthorizedController {

    private final AuthorizationService authorizationService;

    public UnauthorizedController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/addPowerOfAttorney")
    public ResponseEntity<String> addPowerOfAttorney(@RequestBody PowerOfAttorney powerOfAttorney) {
        Optional<User> returnUser = authorizationService.addPowerOfAttorney(powerOfAttorney);
        if (returnUser.isPresent()) {
            User user = returnUser.get();
            return ResponseEntity.ok("User " + user.getUserName() + " created. PIN = " + user.getPin());
        }
        return ResponseEntity.ok("power of attorney added");
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserEntity> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorizationService.insertUser(user));
    }
}
