package com.gazab.chatServer.users;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gazab.chatServer.exceptions.UserNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JWTService {
    
    @Autowired
    private UserRepository userRepository;

    @Value("${token.expiry:30}")
    private int tokenValidityInMinutes;

    @Value("${token.secret:none}")
    private String tokenSecret;

    public String generateToken(String username) {
        log.debug("generating token for username {}", username);
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(!userOpt.isPresent()) {
            log.error("user not found with username: " + username);
            throw new UserNotFoundException("user not found with username: " + username);
        }

        return generateToken(userOpt.get());
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return createToken(user.getUsername(), claims);
    }

    private String createToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+1000*60*tokenValidityInMinutes))
            .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes())).compact();
    }

    public String extractUsername(String token) {
        if(!StringUtils.hasText(token)) {
            log.warn("Empty token received in request");
            return null;
        }

        try {
            log.debug("trying to extract username from token");
            return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes())).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception ex) {
            log.error("can't retrieve username from token", ex);
        }

        return null;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        log.debug("validating token");
        if(!userDetails.getUsername().equals(extractUsername(token))) {
            log.debug("token validation failed: username didn't match");
            return false;
        }

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes())).build().parseClaimsJws(token).getBody();
            if(!((User)userDetails).getRole().equals(claims.get("role"))) {
                log.debug("token validation failed: role didn't match");
                return false;
            }

            if(claims.getExpiration().compareTo(new Date()) < 0) {
                log.debug("token validation failed: token expired");
                return false;
            }

            return true;
        } catch(Exception ex) {
            log.error("token validation failed due to unknown reason", ex);
        }

        return false;
    }
}
