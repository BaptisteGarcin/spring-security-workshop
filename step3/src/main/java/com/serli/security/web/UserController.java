package com.serli.security.web;

import com.serli.security.config.JwtConfig;
import com.serli.security.model.AuthToken;
import com.serli.security.model.User;
import com.serli.security.model.UserRepository;
import com.serli.security.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserRepository userRepository;
    private UserService userService;
    private EntityManager em;
    private JdbcTemplate jdbcTemplate;

    public UserController(UserRepository userRepository, EntityManager em, JdbcTemplate template) {
        this.em = em;
        this.userRepository = userRepository;
        this.jdbcTemplate = template;
    }


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtConfig jwtTokenUtil;

    @GetMapping("/user/current")
    ResponseEntity<?> getUserConnected() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(user);
    }


    @PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody User loginUser) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final User user = (User) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(loginUser.getUsername());
        claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenUtil.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenUtil.getSecret())
                .compact();
        return ResponseEntity.ok(new AuthToken(token));

    }


}