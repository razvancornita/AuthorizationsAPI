package com.authorization.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    @Id
    String id;

    @Indexed(unique = true)
    String accountNumber;

    String accountHolderName;

    Double balance;

    List<GranteeUserEntity> granteeUsers;

    public AccountEntity(String accountNumber, String accountHolderName, Double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }
}
