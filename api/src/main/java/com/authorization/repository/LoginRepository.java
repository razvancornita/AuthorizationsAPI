package com.authorization.repository;

import com.authorization.mongo.entity.LoginEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends MongoRepository<LoginEntity, String> {
    Optional<LoginEntity> findByName(String name);
}