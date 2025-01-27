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

import org.springframework.lang.NonNull;

/**
 * Service interface to retrieve public/private jwt keys.
 */
public interface VaultService {
    // TODO: In deployment, make profiles in Vault, which will change the endpoint for private key (and security probs)
    final static String VAULT_ENDPOINT_JWT_PRIVATE_KEY = "secret/security";
    final static String VAULT_ENDPOINT_JWT_PUBLIC_KEY = "secret/public";
    final static String VAULT_KEY_JWT_PRIVATE_KEY = "jwtPrivateKey";
    final static String VAULT_KEY_JWT_PUBLIC_KEY = "jwtPublicKey";

    /**
     * Retrieve the JWT public key.
     *
     * @return JWT public key
     */
    @NonNull
	public String getJwtPublicKey();

    /**
     * Retrieve the JWT private key.
     *
     * @return JWT private key
     */
    @NonNull
    public String getJwtPrivateKey();
}
