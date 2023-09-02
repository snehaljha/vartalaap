package com.gazab.chatServer.users;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gazab.chatServer.exceptions.UserNotFoundException;
import com.gazab.chatServer.exceptions.UsernameAlreadyExistsException;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private Logger logger;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger = LoggerFactory.getLogger(getClass());
    }

    public void createUser(User user) {
        try {
            userRepository.insert(user);
        } catch(DuplicateKeyException ex) {
            logger.error("username " + user.getUsername() + " already exists", ex);
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        logger.debug("user {} inserted", user.getUsername());
    }

    public User getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(!userOpt.isPresent()) {
            logger.error("user not found with username: " + username);
            throw new UserNotFoundException("user not found with username: " + username);
        }

        logger.debug("retrieved user  with username: {}", username);

        return userOpt.get();
    }

    public void deleteUserByUsername(String username) {
        userRepository.deleteUserByUsername(username);
        logger.debug("deleted user with username {}", username);
    }

    public void updateUser(User user) {
        userRepository.save(user);
        logger.debug("updated user: [{}]{}", user.getId(), user.getUsername());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(!userOpt.isPresent()) {
            throw new UserNotFoundException("User not found with username: " + username);
        }

        return userOpt.get();
    }
}
