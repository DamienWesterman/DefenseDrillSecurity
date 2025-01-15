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

package com.damienwesterman.defensedrill.security.web.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.damienwesterman.defensedrill.security.entity.UserEntity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: Doc comments
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @NotEmpty
    @Size(min = 6, max = 31)
    private String name;
    @NotEmpty
    @Size(min = 8, max = 31)
    private String password;

    @NotNull
    @Size(max = 511)
    /** Comma separated list of roles */
    private String roles;

    /**
     * TODO: Doc comments
     *
     * @param id
     * @param passwordEncoder
     * @return
     */
    public UserEntity toEntity(Long id, PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
            .id(id)
            .name(this.name)
            .password(passwordEncoder.encode(this.password))
            .roles(roles)
            .build();
    }
}
