package com.authorization;

import com.authorization.mongo.MongoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MongoConfiguration.class)
public class AuthorizationApplication {
    public static void main(final String[] args) {
        SpringApplication.run(AuthorizationApplication.class, args);
    }
}
