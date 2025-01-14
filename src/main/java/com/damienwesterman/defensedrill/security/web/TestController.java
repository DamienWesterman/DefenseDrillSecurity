package com.damienwesterman.defensedrill.security.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.security.entity.UserEntity;
import com.damienwesterman.defensedrill.security.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final UserService service;

    @PostMapping("/")
    public ResponseEntity<UserEntity> create(@RequestBody @Valid UserEntity user) {
        return ResponseEntity.ok(service.create(user));
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
    public ResponseEntity<UserEntity> update(@PathVariable Long id, @RequestBody @Valid UserEntity user) {
        user.setId(id);
        return ResponseEntity.ok(service.update(user));
    }

    @GetMapping("/encrypt/{string}")
    public String get(@PathVariable String string) {
        return new BCryptPasswordEncoder().encode(string);
    }
}
