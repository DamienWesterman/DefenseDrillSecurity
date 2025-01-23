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

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.security.entity.UserEntity;

import lombok.RequiredArgsConstructor;

/**
 * Service class for loading a user's info by their username.
 */
@Service
@RequiredArgsConstructor
public class DrillUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optUser = userService.find(username);

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        UserEntity user = optUser.get();
        return User.builder()
            .username(user.getName())
            .password(user.getPassword())
            .roles(getRolesAsList(user))
            .build();
    }

    /**
     * Split a string of roles into a list of string roles. Does validation checking.
     *
     * @param user User
     * @return List of roles.
     */
    private String[] getRolesAsList(UserEntity user) throws UsernameNotFoundException {
        if (null == user.getRoles() || user.getRoles().isEmpty()) {
            throw new UsernameNotFoundException(user.getName());
        }

        return user.getRoles().split(",");
    }
}
