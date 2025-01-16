/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2025 Damien Westerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.damienwesterman.defensedrill.security.web;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.security.entity.UserEntity;
import com.damienwesterman.defensedrill.security.service.UserService;
import com.damienwesterman.defensedrill.security.web.dto.UserCreateDTO;
import com.damienwesterman.defensedrill.security.web.dto.UserInfoDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(UsersController.ENDPOINT)
@RequiredArgsConstructor
public class UsersController {
    public static final String ENDPOINT = "/user";
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<UserInfoDTO>> getAllUsers() {
        List<UserEntity> users = service.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
            users.stream()
                .map(UserInfoDTO::new)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<UserInfoDTO> createUser(@RequestBody @Valid UserCreateDTO user) {
        UserEntity createdUser = service.create(user.toEntity(null, passwordEncoder));
        return ResponseEntity
            .created(URI.create(ENDPOINT + "/" + createdUser.getId()))
            .body(new UserInfoDTO(createdUser)
        );
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserInfoDTO> getUserById(@PathVariable Long id) {
        Optional<UserEntity> optUser = service.find(id);

        if (optUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserInfoDTO(optUser.get()));
    }
}
