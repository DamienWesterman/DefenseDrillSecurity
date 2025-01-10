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

package com.damienwesterman.defensedrill.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.security.entity.UserEntity;
import com.damienwesterman.defensedrill.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for database CRUD operations for users.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    /**
     * Create a new user.
     *
     * @param user User to save to the database.
     * @return Newly created User.
     */
    @NonNull
    public UserEntity create(@NonNull UserEntity user) {
        user.setId(null);
        return repo.save(user);
    }

    // TODO: all doc comments
    public Optional<UserEntity> find(@NonNull Long id) {
        return repo.findById(id);
    }

    public Optional<UserEntity> find(@NonNull String name) {
        return repo.findByName(name);
    }

    @NonNull
    public List<UserEntity> findAll() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    // TODO: FIXME: start here, finish this method and the rest of this class
    @NonNull
    public List<UserEntity> findAllByRole(@NonNull String role) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        UserEntity user = UserEntity.builder()
            .roles(role)
            .build();
        return repo.findAll(
            Example.of(user, matcher),
            Sort.by(Sort.Direction.ASC, "name"));
    }
}
