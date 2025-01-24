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
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import lombok.RequiredArgsConstructor;
/**
 * Service class to interact with the HashiCorp Vault KMS.
 */
@Service
@RequiredArgsConstructor
public class VaultService {
    private final VaultTemplate vaultTemplate;

    // TODO: In deployment, make profiles in Vault, which will change the endpoint for private key (and security probs)
    private final static String VAULT_ENDPOINT_JWT_PRIVATE_KEY = "secret/security";
    private final static String VAULT_ENDPOINT_JWT_PUBLIC_KEY = "secret/public";
    private final static String VAULT_KEY_JWT_PRIVATE_KEY = "jwtPrivateKey";
    private final static String VAULT_KEY_JWT_PUBLIC_KEY = "jwtPublicKey";

    /**
     * Retrieve the JWT public key.
     *
     * @return JWT public key
     */
    @NonNull
	public String getJwtPublicKey() {
        VaultResponse response = vaultTemplate.read(VAULT_ENDPOINT_JWT_PUBLIC_KEY);

        if (null == response || null == response.getData()) {
            throw new RuntimeException("Failed to get public key from fault, please check Vault and restart server");
        }

        return (String) response.getData().get(VAULT_KEY_JWT_PUBLIC_KEY);
	}

    /**
     * Retrieve the JWT private key.
     *
     * @return JWT private key
     */
    @NonNull
    public String getJwtPrivateKey() {
        VaultResponse response = vaultTemplate.read(VAULT_ENDPOINT_JWT_PRIVATE_KEY);

        if (null == response || null == response.getData()) {
            throw new RuntimeException("Failed to get private key from fault, please check Vault and restart server");
        }

        return (String) response.getData().get(VAULT_KEY_JWT_PRIVATE_KEY);
    }
}
