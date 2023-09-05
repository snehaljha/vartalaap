package com.gazab.chatServer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.gazab.chatServer.filter.JWTAuthFilter;
import com.gazab.chatServer.users.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApplicationConfiguration {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTAuthFilter authFilter;

    // for without jwt
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    //     return http.authorizeHttpRequests((req) -> {
    //             req.requestMatchers(new AntPathRequestMatcher("/test/open")).permitAll();
    //             req.requestMatchers(new AntPathRequestMatcher("/**")).authenticated();
    //         }).formLogin(Customizer.withDefaults()).authenticationProvider(getAuthenticationProvider()).build();
    // }

    // for jwt
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf((csrf) -> csrf.disable()).authorizeHttpRequests((req) -> {
                req.requestMatchers(HttpMethod.POST, "/login", "/user").permitAll();
                req.requestMatchers(new AntPathRequestMatcher("/test/open")).permitAll();
            })
            .authorizeHttpRequests((req) -> {
                req.anyRequest().authenticated();
            }).sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(getAuthenticationProvider()).addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(getPasswordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
