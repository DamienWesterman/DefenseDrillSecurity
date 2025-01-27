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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.security.util.Constants;
import com.damienwesterman.defensedrill.security.util.Constants.UserRoles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for JWT interaction. Provides methods to generate, check, and interpret JWT.
 */
@Service
@Slf4j
public class JwtService {
    private static final long MILLIS_VALID_USER = TimeUnit.DAYS.toMillis(31);
    private static final long MILLIS_VALID_ADMIN = TimeUnit.MINUTES.toMillis(30);
    private static final String CLAIMS_KEY_ROLES = "roles";

    /**
     * Check if a JWT is valid.
     *
     * @param jwt String JWT
     * @return true/false if the token is valid
     */
    public boolean isTokenValid(String jwt) {
        return Optional.ofNullable(getClaims(jwt)).isPresent();
    }

    /**
     * Extract a user's username from a JWT.
     *
     * @param jwt String JWT
     * @return Username
     */
    @NonNull
    public String extractUsername(String jwt) {
        return Optional.ofNullable(getClaims(jwt))
            .map(Claims::getSubject)
            .orElse("");
    }

    /**
     * Extract a user's roles from a JWT.
     *
     * @param jwt String JWT
     * @return Roles concatenated into a single string using ","
     */
    @NonNull
    public String extractRoles(String jwt) {
        return Optional.ofNullable(getClaims(jwt))
            .map(claims -> (String) claims.get(CLAIMS_KEY_ROLES))
            .orElse("");
    }

    /**
     * Get a long value of the milliseconds a JWT [cookie] should be valid from
     * a user's roles.
     *
     * @param roles Roles concatenated into a single string using ","
     * @return Long of the milliseconds a user's JWT should be valid, 0 on error
     */
    public long getMillisValid(String roles) {
        if (null != roles && !roles.isEmpty()) {
            // Check in descending order of millis valid, most restrictive when possible
            if (roles.contains(UserRoles.ADMIN.getStringRepresentation())) {
                return MILLIS_VALID_ADMIN;
            } else if (roles.contains(UserRoles.USER.getStringRepresentation())) {
                return MILLIS_VALID_USER;
            }
        }

        return 0;
    }

    /**
     * Generate a string JWT from a UserDetails object.
     *
     * @param userDetails UserDetails object
     * @return String containing the generated JWT
     */
    @NonNull
    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", Constants.JWT_ISSUER);
        String roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        claims.put(CLAIMS_KEY_ROLES, roles);

        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(getMillisValid(roles))))
            .signWith(generatePrivateKey())
            .compact();
    }

    /**
     * Extract the claims from a JWT string. May return null on error.
     *
     * @param jwt String JWT
     * @return Claims object, may be null on error
     */
    @Nullable
    private Claims getClaims(String jwt) {
        if (null == jwt || jwt.isBlank()) {
            return null;
        }

        try {
            return Jwts.parser()
                .verifyWith(generatePublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        } catch (JwtException e) {
            log.warn(e.getMessage());
        }

        return null;
    }

    private PublicKey generatePublicKey() {
        byte[] decodedKey = Base64.getDecoder().decode(Constants.PUBLIC_KEY);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Error during public key generation", e);
            throw new RuntimeException(e);
        }
    }

    private PrivateKey generatePrivateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(Constants.PRIVATE_KEY);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Error during private key generation", e);
            throw new RuntimeException(e);
        }
    }
}
