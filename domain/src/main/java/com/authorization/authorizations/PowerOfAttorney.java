package com.authorization.authorizations;

import com.authorization.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PowerOfAttorney {
    String granteeName; //"new" owner
    String grantorName; //owner
    Account account;
    Authorization authorization;
}
