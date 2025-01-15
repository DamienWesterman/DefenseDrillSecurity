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

package com.damienwesterman.defensedrill.security.util;

import java.util.List;
import java.util.stream.Collectors;

public class Constants {
    public static enum UserRoles {
        USER("USER"),
        ADMIN("ADMIN");

        private String roleString;

        UserRoles(String roleString) {
            this.roleString = roleString;
        }

        public String getStringRepresentation() {
            return this.roleString;
        }
    }

    public static final List<String> ALL_ROLES_LIST = List.of(UserRoles.values()).stream()
        .map(UserRoles::getStringRepresentation)
        .collect(Collectors.toList());
}
