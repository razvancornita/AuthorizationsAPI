package com.authorization.account;

import com.authorization.authorizations.Authorization;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GrantedAccount extends Account {
    List<Authorization> authorizations;
}
