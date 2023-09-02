package com.gazab.chatServer.users;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findUserByUsername(String username);

    void deleteUserByUsername(String username);
}
