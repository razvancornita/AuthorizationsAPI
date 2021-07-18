package com.authorization.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "login")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginEntity {
    @Id
    String name;

    Boolean loggedIn;
}
