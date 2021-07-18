package com.authorization.mongo.entity;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "user")
@Value
@Builder
public class UserEntity {
    @Id
    String id;

    @Indexed(unique = true)
    String name;

    String pin;

    List<GranteeAccount> granteeAccountIds;
}
