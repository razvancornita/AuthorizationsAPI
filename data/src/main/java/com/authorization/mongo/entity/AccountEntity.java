package com.authorization.mongo.entity;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "account")
@Value
@Builder
public class AccountEntity {
    @Id
    String id;

    @Indexed(unique = true)
    String accountNumber;

    String accountHolderName;

    Double balance;
}
