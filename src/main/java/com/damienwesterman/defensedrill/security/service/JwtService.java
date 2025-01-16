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

import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.security.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: Doc comments (on all methods too)
 */
@Service
@Slf4j
public class JwtService {
    private static final long MOBILE_MILLIS_VALID = TimeUnit.DAYS.toMillis(31);
    private static final long WEB_MILLIS_VALID = TimeUnit.MINUTES.toMillis(30);
    private static final String CLAIMS_KEY_ROLES = "roles";

    public String generateMobileToken(UserDetails userDetails) {
        return generateTokenInternal(userDetails, MOBILE_MILLIS_VALID);
    }

    public String generateWebToken(UserDetails userDetails) {
        return generateTokenInternal(userDetails, WEB_MILLIS_VALID);
    }

    public boolean isTokenValid(String jwt) {
        return Optional.ofNullable(getClaims(jwt)).isPresent();
    }

    public String extractUsername(String jwt) {
        return Optional.ofNullable(getClaims(jwt))
            .map(Claims::getSubject)
            .orElse("");
    }

    public String extractRoles(String jwt) {
        return Optional.ofNullable(getClaims(jwt))
            .map(claims -> (String) claims.get(CLAIMS_KEY_ROLES))
            .orElse("");
    }

    // TODO: Expose this and change teh millisValid to be based on the role, maybe add a mobile role
    private String generateTokenInternal(UserDetails userDetails, long millisValid) {
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
            .expiration(Date.from(Instant.now().plusMillis(millisValid)))
            .signWith(generatePrivateKey())
            .compact();
    }

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
