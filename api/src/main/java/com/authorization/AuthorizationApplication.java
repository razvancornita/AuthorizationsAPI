package com.authorization;

import com.authorization.mongo.config.MongoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@Import(MongoConfiguration.class)
@EnableMongoRepositories
public class AuthorizationApplication {
    public static void main(final String[] args) {
        SpringApplication.run(AuthorizationApplication.class, args);
    }
}
