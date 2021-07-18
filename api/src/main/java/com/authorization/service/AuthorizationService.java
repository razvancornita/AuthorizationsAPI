package com.authorization.service;


import com.authorization.account.Account;
import com.authorization.authorizations.Authorization;
import com.authorization.authorizations.PowerOfAttorney;
import com.authorization.mongo.entity.AccountEntity;
import com.authorization.mongo.entity.GranteeAccount;
import com.authorization.mongo.entity.UserEntity;
import com.authorization.security.SecurityUtility;
import com.authorization.user.User;
import com.authorization.util.ValidatorUtil;
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

    private final SecurityUtility securityUtility;
    private final DatabaseService databaseService;

    public AuthorizationService(SecurityUtility securityUtility, DatabaseService databaseService) {
        this.securityUtility = securityUtility;
        this.databaseService = databaseService;
    }

    public List<AccountEntity> getAllAccounts() {
        String username = securityUtility.getUserName();
        UserEntity userEntity = databaseService.getUserByUsername(username).orElseThrow(() ->
                new NoSuchElementException("logged in user doesn't have any accounts"));
        List<GranteeAccount> granteeAccountIds = userEntity.getGranteeAccountIds();

        if (granteeAccountIds == null) {
            return Collections.emptyList();
        }
        return getAllGranteeAccounts(granteeAccountIds);
    }

    public Optional<User> addPowerOfAttorney(PowerOfAttorney powerOfAttorney) {
        Account accountFromRequest = powerOfAttorney.getAccount();
        ValidatorUtil.validateAccountOwner(powerOfAttorney, accountFromRequest);

        AccountEntity accountEntity = databaseService.getAccountByAccountNumber(accountFromRequest.getAccountNumber());
        Optional<UserEntity> granteeUser = databaseService.getUserByUsername(powerOfAttorney.getGranteeName());

        if (granteeUser.isEmpty()) { //grantee does not have an account -> we create one
            return Optional.of(createAndInsertUser(powerOfAttorney, accountEntity));
        } else {
            UserEntity user = granteeUser.get();
            List<GranteeAccount> granteeAccounts = user.getGranteeAccountIds();
            Optional<GranteeAccount> existingAccount = granteeAccounts.stream().filter(granteeAccount ->
                    accountEntity.getId().equals(granteeAccount.getAccountId())).findFirst();

            if (existingAccount.isPresent()) {
                GranteeAccount granteeAccount = existingAccount.get();
                addRightToExistingGrantee(powerOfAttorney.getAuthorization(), granteeAccount);
                databaseService.saveUser(user);
            } else {
                GranteeAccount granteeAccount = createGranteeAccount(powerOfAttorney, accountEntity);
                granteeAccounts.add(granteeAccount);
            }
            return Optional.empty();
        }
    }

    private void addRightToExistingGrantee(Authorization authorization, GranteeAccount granteeAccount) {
        if (authorization == Authorization.READ) {
            granteeAccount.setCanRead(true);
        } else if (authorization == Authorization.WRITE) {
            granteeAccount.setCanWrite(true);
        }
    }


    private User createAndInsertUser(PowerOfAttorney powerOfAttorney, AccountEntity accountEntity) {
        GranteeAccount granteeAccount = createGranteeAccount(powerOfAttorney, accountEntity);
        String randomPin = String.format("%04d", new Random().nextInt(10000));

        UserEntity userEntity = UserEntity.builder()
                .name(powerOfAttorney.getGranteeName())
                .pin(randomPin)
                .granteeAccountIds(Collections.singletonList(granteeAccount))
                .build();

        databaseService.saveUser(userEntity);
        return new User(powerOfAttorney.getGranteeName(), randomPin);
    }


    private GranteeAccount createGranteeAccount(PowerOfAttorney powerOfAttorney, AccountEntity accountEntity) {
        GranteeAccount granteeAccount = new GranteeAccount();
        granteeAccount.setAccountId(accountEntity.getId());
        addRightToExistingGrantee(powerOfAttorney.getAuthorization(), granteeAccount);
        return granteeAccount;
    }

    private List<AccountEntity> getAllGranteeAccounts(List<GranteeAccount> granteeAccountIds) {
        List<String> accountIds = granteeAccountIds.stream().map(GranteeAccount::getAccountId).collect(Collectors.toList());
        return Streamable.of(databaseService.getAllAccountsById(accountIds)).stream().collect(Collectors.toList());
    }

    public AccountEntity insertAccount(Account account) {
        String username = securityUtility.getUserName();
        ValidatorUtil.validateUsernameFromToken(username, account);
        return databaseService.insertAccount(account, username);
    }
}