package com.authorization.service;


import com.authorization.account.Account;
import com.authorization.authorizations.Authorization;
import com.authorization.authorizations.PowerOfAttorney;
import com.authorization.exception.UnauthorizedException;
import com.authorization.mongo.entity.AccountEntity;
import com.authorization.mongo.entity.GranteeAccount;
import com.authorization.mongo.entity.UserEntity;
import com.authorization.repository.AccountRepository;
import com.authorization.repository.UserRepository;
import com.authorization.security.SecurityUtility;
import com.authorization.user.User;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SecurityUtility securityUtility;

    public AuthorizationService(AccountRepository userRepository, UserRepository userRepository1, SecurityUtility securityUtility) {
        this.accountRepository = userRepository;
        this.userRepository = userRepository1;
        this.securityUtility = securityUtility;
    }

    public UserEntity getUserFromDb(String username) {
        return userRepository.findByName(username).orElseThrow(NoSuchElementException::new);
    }

    public List<AccountEntity> getAllAccounts() {
        String userName = securityUtility.getUserName();
        UserEntity userEntity = userRepository.findByName(userName).orElseThrow(() ->
                new NoSuchElementException("logged in user doesn't have any accounts"));
        List<GranteeAccount> granteeAccountIds = userEntity.getGranteeAccountIds();

        if (granteeAccountIds == null) {
            return Collections.emptyList();
        }
        return getAllGranteeAccounts(granteeAccountIds);


    }

    public Optional<User> addPowerOfAttorney(PowerOfAttorney powerOfAttorney) {
        Account accountFromRequest = powerOfAttorney.getAccount();
        validateAccountOwner(powerOfAttorney, accountFromRequest);

        AccountEntity accountEntity = accountRepository.findByAccountNumber(accountFromRequest.getAccountNumber())
                .orElseThrow(() -> new NoSuchElementException("account not found"));

        Optional<UserEntity> granteeUser = userRepository.findByName(powerOfAttorney.getGranteeName());

        if (granteeUser.isEmpty()) {
            return Optional.of(createAndInsertUser(powerOfAttorney, accountEntity));
        } else {
            UserEntity user = granteeUser.get();
            List<GranteeAccount> granteeAccounts = user.getGranteeAccountIds();
            Optional<GranteeAccount> existingAccount = granteeAccounts.stream().filter(granteeAccount ->
                    accountEntity.getId().equals(granteeAccount.getAccountId())).findFirst();

            if (existingAccount.isPresent()) {
                GranteeAccount granteeAccount = existingAccount.get();
                addRightToExisting(powerOfAttorney, granteeAccount);
                userRepository.save(user);
            } else {
                GranteeAccount granteeAccount = createGranteeAccount(powerOfAttorney, accountEntity);
                granteeAccounts.add(granteeAccount);
            }
            return Optional.empty();
        }
    }

    private void addRightToExisting(PowerOfAttorney powerOfAttorney, GranteeAccount granteeAccount) {
        if (powerOfAttorney.getAuthorization() == Authorization.READ) {
            granteeAccount.setCanRead(true);
        } else if (powerOfAttorney.getAuthorization() == Authorization.WRITE) {
            granteeAccount.setCanWrite(true);
        }
    }

    private void validateAccountOwner(PowerOfAttorney powerOfAttorney, Account accountFromRequest) {
        if (!accountFromRequest.getAccountHolderName().equals(powerOfAttorney.getGrantorName())) {
            throw new UnauthorizedException("Account owner names do not match");
        }
    }

    private User createAndInsertUser(PowerOfAttorney powerOfAttorney, AccountEntity accountEntity) {
        GranteeAccount granteeAccount = createGranteeAccount(powerOfAttorney, accountEntity);
        var randomPin = String.format("%04d", new Random().nextInt(10000));

        UserEntity userEntity = UserEntity.builder()
                .name(powerOfAttorney.getGranteeName())
                .pin(randomPin)
                .granteeAccountIds(Collections.singletonList(granteeAccount))
                .build();

        userRepository.save(userEntity);
        return new User(powerOfAttorney.getGranteeName(), randomPin);
    }


    private GranteeAccount createGranteeAccount(PowerOfAttorney powerOfAttorney, AccountEntity accountEntity) {
        GranteeAccount granteeAccount = new GranteeAccount();
        granteeAccount.setAccountId(accountEntity.getId());
        addRightToExisting(powerOfAttorney, granteeAccount);
        return granteeAccount;
    }

    private List<AccountEntity> getAllGranteeAccounts(List<GranteeAccount> granteeAccountIds) {
        List<String> accountIds = granteeAccountIds.stream().map(GranteeAccount::getAccountId).collect(Collectors.toList());
        return Streamable.of(accountRepository.findAllById(accountIds)).stream().collect(Collectors.toList());
    }

    public AccountEntity insertAccount(Account account) {
        String userName = securityUtility.getUserName();
        if (!userName.equals(account.getAccountHolderName())) {
            throw new UnauthorizedException("Account owner names do not match");
        }
        AccountEntity accountEntity = AccountEntity.builder()
                .accountNumber(account.getAccountNumber())
                .accountHolderName(userName)
                .balance(account.getBalance()).build();
        return accountRepository.save(accountEntity);
    }

    public UserEntity insertUser(User user) {
        UserEntity userEntity = UserEntity.builder()
                .name(user.getUserName())
                .pin(String.format("%04d", new Random().nextInt(10000)))
                .build();
        return userRepository.save(userEntity);
    }
}