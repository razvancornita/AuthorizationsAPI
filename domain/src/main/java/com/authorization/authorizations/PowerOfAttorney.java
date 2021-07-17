package com.authorization.authorizations;

import com.authorization.account.Account;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PowerOfAttorney {
    String granteeName;
    String grantorName;
    Account account;
    Authorization authorization;
}
