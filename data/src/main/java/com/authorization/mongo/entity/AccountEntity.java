package com.authorization.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "account")
@Data
@AllArgsConstructor
public class AccountEntity {
    @Id
    String id;

    @Indexed(unique = true)
    String accountNumber;

    String accountHolderName;

    Double balance;

    public AccountEntity(String accountNumber, String accountHolderName, Double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }
}
