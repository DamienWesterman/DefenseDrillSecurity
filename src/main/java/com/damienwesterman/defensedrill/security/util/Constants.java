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

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.damienwesterman.defensedrill.security.service.VaultService;

@Component
public class Constants {
    /**
     * Constructor for spring to fill constants at startup.
     */
    private Constants(Environment environment, VaultService vaultService) {
        ACTIVE_SPRING_PROFILES = List.of(environment.getActiveProfiles());

        PUBLIC_KEY = vaultService.getJwtPublicKey();
        PRIVATE_KEY = vaultService.getJwtPrivateKey();
    }

    private static List<String> ACTIVE_SPRING_PROFILES = null;
    private static final String PROD_PROFILE_STRING = "prod";

    /**
     * Checks the spring profile to see if we are a production environment. True by default.
     *
     * @return true if production server
     */
    public static boolean isProductionServer() {
        if (null == ACTIVE_SPRING_PROFILES) {
            return true;
        }

        return ACTIVE_SPRING_PROFILES.contains(PROD_PROFILE_STRING);
    }

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

    public static String PUBLIC_KEY = null;
    public static String PRIVATE_KEY = null;

    public static final String JWT_ISSUER = "DefenseDrillWeb";
}
