package com.damienwesterman.defensedrill.security.web;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.security.entity.UserEntity;
import com.damienwesterman.defensedrill.security.service.DrillUserDetailsService;
import com.damienwesterman.defensedrill.security.service.JwtService;
import com.damienwesterman.defensedrill.security.service.UserService;
import com.damienwesterman.defensedrill.security.web.dto.UserDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final DrillUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<UserEntity> create(@RequestBody @Valid UserDTO user) {
        return ResponseEntity.ok(service.create(user.toEntity(null, passwordEncoder)));
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/allAdmins")
    public ResponseEntity<List<UserEntity>> getAdmins() {
        return ResponseEntity.ok(service.findAllByRole("admin"));
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserEntity> update(@PathVariable Long id, @RequestBody @Valid UserDTO user) {
        return ResponseEntity.ok(service.update(user.toEntity(id, passwordEncoder)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/home")
    public String home() {
        return "Home Page";
    }

    @GetMapping("/user")
    public String user() {
        return "User Page";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }

    @GetMapping("/key")
    public String generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair key = keyGen.generateKeyPair();
        StringBuilder sb = new StringBuilder();
        sb.append("Public Key: ");
        sb.append(Base64.getEncoder().encodeToString(key.getPublic().getEncoded()));
        sb.append("<br><br>");
        sb.append("Private Key: ");
        sb.append(Base64.getEncoder().encodeToString(key.getPrivate().getEncoded()));
        return sb.toString();
    }

    @GetMapping("/jwt/{username}")
    public String generateJwt(@PathVariable String username) {
        return jwtService.generateMobileToken(userDetailsService.loadUserByUsername(username));
    }

    @GetMapping("/jwt/check/{jwt}")
    public String checkJwt(@PathVariable String jwt) {
        return Boolean.toString(jwtService.isTokenValid(jwt));
    }
}
