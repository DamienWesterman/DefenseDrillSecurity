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

package com.damienwesterman.defensedrill.security.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.security.service.DrillUserDetailsService;
import com.damienwesterman.defensedrill.security.service.JwtService;
import com.damienwesterman.defensedrill.security.web.dto.LoginDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(RestAuthenticationController.ENDPOINT)
@RequiredArgsConstructor
public class RestAuthenticationController {
    private static final String ENDPOINT = "/authenticate";

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DrillUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<String> authenticate(@RequestBody LoginDTO login) {
        // Have to surround in a try/catch, otherwise Spring will follow default security response
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );

            if (!authentication.isAuthenticated()) {
                throw new UsernameNotFoundException("Invalid Credentials");
            }

            return ResponseEntity.ok(
                jwtService.generateToken(
                    userDetailsService.loadUserByUsername(login.getUsername())
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
        }
    }

    /**
     * User wants to authenticate and get a JWT for a specific role.
     * <br><br>
     * An example for this might be someone who is a mobile user granted ROLE_USER and ROLE_ADMIN.
     * The user wants the extended expiration for their JWT using ROLE_USER, so this endpoint allows
     * them to specify which role they want to authenticate for.
     *
     * @param login User Login DTO
     * @param role Role to authenticate for
     * @return ResponseEntity containing the String JWT
     */
    @PostMapping("/{role}")
    public ResponseEntity<String> authenticateForRole(@RequestBody LoginDTO login, @PathVariable String role) {
        // Have to surround in a try/catch, otherwise Spring will follow default security response
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );

            if (!authentication.isAuthenticated()) {
                throw new UsernameNotFoundException("Invalid Credentials");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(login.getUsername());

            // Remove all roles that do not match the requested role
            String prefixedRole = "ROLE_" + role;
            List<GrantedAuthority> authorities = userDetails.getAuthorities().stream()
                .filter(grantedRole -> grantedRole.getAuthority().equalsIgnoreCase(prefixedRole))
                .collect(Collectors.toList());

            if (authorities.isEmpty()) {
                // They either requested a role that doesn't exist or are not authorized for that role
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UserDetails modifiedUser = User.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .authorities(authorities)
                .build();

            return ResponseEntity.ok(jwtService.generateToken(modifiedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
        }
    }
}
