package com.authorization.service;


import com.authorization.account.Account;
import com.authorization.exception.UnauthorizedException;
import com.authorization.mongo.entity.AccountEntity;
import com.authorization.mongo.entity.UserEntity;
import com.authorization.repository.AccountRepository;
import com.authorization.repository.UserRepository;
import com.authorization.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Service
public class DatabaseService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public DatabaseService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByName(username);
    }

    public AccountEntity getAccountByAccountNumber(Account account) {
        AccountEntity accountEntity = accountRepository.findByAccountNumber(account.getAccountNumber()).orElseThrow(() ->
                new NoSuchElementException("account not found"));

        if (!accountEntity.getAccountHolderName().equals(account.getAccountHolderName())) {
            throw new UnauthorizedException("AccountHolder and AccountNumber do not match");
        }
        return accountEntity;
    }

    public UserEntity saveUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public AccountEntity saveAccount(AccountEntity accountEntity) {
        return accountRepository.save(accountEntity);
    }

    public Iterable<AccountEntity> getAllAccountsById(List<String> ids) {
        return accountRepository.findAllById(ids);
    }

    public UserEntity insertUser(User user) {
        UserEntity userEntity = UserEntity.builder()
                .name(user.getUserName())
                .pin(String.format("%04d", new Random().nextInt(10000)))
                .build();
        return saveUser(userEntity);
    }

    public AccountEntity insertAccount(Account account, String userName) {
        AccountEntity accountEntity = new AccountEntity(account.getAccountNumber(), userName, account.getBalance());
        return saveAccount(accountEntity);
    }

    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }
}