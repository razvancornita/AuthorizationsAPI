package com.authorization.util;

import com.authorization.account.Account;
import com.authorization.authorizations.PowerOfAttorney;
import com.authorization.exception.UnauthorizedException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidatorUtil {

    public static void validateUsernameFromToken(String username, Account account) {
        if (!username.equals(account.getAccountHolderName())) {
            throw new UnauthorizedException("Account owner names do not match");
        }
    }

    public static void validateAccountOwner(PowerOfAttorney powerOfAttorney, Account accountFromRequest) {
        if (!accountFromRequest.getAccountHolderName().equals(powerOfAttorney.getGrantorName())) {
            throw new UnauthorizedException("Account owner names do not match");
        }
    }
}
