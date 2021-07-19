package com.authorization.service;


import com.authorization.account.Account;
import com.authorization.authorizations.Authorization;
import com.authorization.authorizations.PowerOfAttorney;
import com.authorization.mongo.entity.AccountEntity;
import com.authorization.mongo.entity.GranteeUserEntity;
import com.authorization.mongo.entity.UserEntity;
import com.authorization.security.SecurityUtility;
import com.authorization.user.User;
import com.authorization.util.ValidatorUtil;
import org.springframework.stereotype.Service;

import java.util.*;

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

        List<AccountEntity> result = new ArrayList<>();
        List<AccountEntity> accounts = databaseService.getAllAccounts();
        for (AccountEntity account : accounts) {
            account.getGranteeUsers().stream()
                    .filter(granteeUser -> granteeUser.getGranteeName().equals(username))
                    .findAny()
                    .ifPresent(foundAccount -> result.add(account));
            account.setGranteeUsers(Collections.emptyList());
        }
        return result;
    }

    public Optional<User> addPowerOfAttorney(PowerOfAttorney powerOfAttorney) {
        Account accountFromRequest = powerOfAttorney.getAccount();
        ValidatorUtil.validateAccountOwner(powerOfAttorney, accountFromRequest);

        AccountEntity accountEntity = databaseService.getAccountByAccountNumber(accountFromRequest);

        Optional<UserEntity> granteeUser = databaseService.getUserByUsername(powerOfAttorney.getGranteeName());

        if (accountEntity.getGranteeUsers() == null) { //0 granteeUsers
            List<GranteeUserEntity> granteeUsers = Collections.singletonList(createGranteeAccount(powerOfAttorney));
            accountEntity.setGranteeUsers(granteeUsers);
        } else {
            Optional<GranteeUserEntity> account = accountEntity.getGranteeUsers().stream()
                    .filter(granteeAccount -> granteeAccount.getGranteeName().equals(powerOfAttorney.getGranteeName()))
                    .findFirst();

            if (account.isPresent()) {
                addRightToExistingGrantee(powerOfAttorney.getAuthorization(), account.get());
            } else {
                accountEntity.getGranteeUsers().add(createGranteeAccount(powerOfAttorney));
            }
        }
        databaseService.saveAccount(accountEntity);

        if (!granteeUser.isPresent()) { //grantee does not have an account -> we create one
            return Optional.of(createAndInsertUser(powerOfAttorney));
        }
        return Optional.empty();
    }

    private void addRightToExistingGrantee(Authorization authorization, GranteeUserEntity granteeUser) {
        if (authorization == Authorization.READ) {
            granteeUser.setCanRead(true);
        } else if (authorization == Authorization.WRITE) {
            granteeUser.setCanWrite(true);
        }
    }


    private User createAndInsertUser(PowerOfAttorney powerOfAttorney) {
        String randomPin = String.format("%04d", new Random().nextInt(10000));

        UserEntity userEntity = UserEntity.builder()
                .name(powerOfAttorney.getGranteeName())
                .pin(randomPin)
                .build();

        databaseService.saveUser(userEntity);
        return new User(powerOfAttorney.getGranteeName(), randomPin);
    }


    private GranteeUserEntity createGranteeAccount(PowerOfAttorney powerOfAttorney) {
        GranteeUserEntity granteeUser = new GranteeUserEntity();
        granteeUser.setGranteeName(powerOfAttorney.getGranteeName());
        addRightToExistingGrantee(powerOfAttorney.getAuthorization(), granteeUser);
        return granteeUser;
    }

    public AccountEntity insertAccount(Account account) {
        String username = securityUtility.getUserName();
        ValidatorUtil.validateUsernameFromToken(username, account);
        return databaseService.insertAccount(account, username);
    }
}