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
import com.damienwesterman.defensedrill.security.util.Constants;

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

        if (!isValidRoles(user.getRoles())) {
            throw new IllegalArgumentException("Roles are not valid");
        }

        return repo.save(user);
    }

    /**
     * Find a user by their ID.
     *
     * @param id User ID.
     * @return Optional containing the user, if one exists.
     */
    public Optional<UserEntity> find(@NonNull Long id) {
        return repo.findById(id);
    }

    /**
     * Find a user by their name.
     *
     * @param name User's name.
     * @return Optional containing the user, if one exists.
     */
    public Optional<UserEntity> find(@NonNull String name) {
        return repo.findByName(name);
    }

    /**
     * Find all users in the database. Returned in alphabetical order by first name.
     *
     * @return
     */
    @NonNull
    public List<UserEntity> findAll() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * Find all users of the given role. Role should be one of {@link Constants.UserRoles}.
     * Returned in alphabetical order by name.
     *
     * @param role String {@link Constants.UserRoles} role.
     * @return List of UserEntity objects.
     */
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

    /**
     * Update a User.
     *
     * @param user User to update.
     * @return Updated User.
     */
    @NonNull
    public UserEntity update(@NonNull UserEntity user) {
        if (null == user.getId()) {
            // This would cause a 'create' operation when repo.save() is called
            throw new NullPointerException("ID is null");
        }

        if (!isValidRoles(user.getRoles())) {
            throw new IllegalArgumentException("Roles are not valid");
        }

        return repo.save(user);
    }

    /**
     * Delete a User by their ID.
     *
     * @param id User ID.
     */
    public void delete(@NonNull Long id) {
        repo.deleteById(id);
    }

    /**
     * Check to make sure that a comma seperated list of roles are all valid. A valid role is one
     * saved within {@link Constants.UserRoles}.
     *
     * @param roles String representation of a comma seperated list of roles.
     * @return true/false if all roles are valid.
     */
    private boolean isValidRoles(@NonNull String roles) {
        if (roles.isBlank()) {
            return true;
        }

        List<String> rolesList = List.of(roles.split(","));

        // Check if each role in rolesList is in the ALL_ROLES_LIST
        return Constants.ALL_ROLES_LIST.containsAll(rolesList);
    }
}
