package com.gazab.chatServer.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.gazab.chatServer.model.AuthRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class UserController {

    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if(authentication.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(authRequest.getUsername()));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value="/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/user")
    public User getUserByUsername(@RequestParam(name = "username") String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping(value="/user")
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/user")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/user")
    public ResponseEntity<Void> deleteUser(@RequestParam(name = "username") String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping(value = "/test/open")
    public String openTest() {
        String password = new BCryptPasswordEncoder().encode("admin");
        return "This an unprotected endpoint\n" + password;
    }
    
    @GetMapping(value = "/test/close")
    public String closeTest() {
        return "This is a protected endpoint";
    }

    @PostMapping(value = "/test/close")
    public String postCloseTest() {
        return "This is a post protected endpoint";
    }
}
