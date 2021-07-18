package com.authorization.controller;

import com.authorization.account.Account;
import com.authorization.mongo.entity.AccountEntity;
import com.authorization.service.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/authorization")
public class AuthorizedController {
    private final AuthorizationService authorizationService;

    public AuthorizedController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/getAllAccounts")
    public List<AccountEntity> getAllAccounts() {
        return authorizationService.getAllAccounts();
    }

    @PostMapping("/createAccount")
    public ResponseEntity<AccountEntity> createAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorizationService.insertAccount(account));
    }
}
