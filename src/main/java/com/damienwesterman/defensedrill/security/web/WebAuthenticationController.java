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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.damienwesterman.defensedrill.security.service.DrillUserDetailsService;
import com.damienwesterman.defensedrill.security.service.JwtService;
import com.damienwesterman.defensedrill.security.util.Constants;
import com.damienwesterman.defensedrill.security.web.dto.LoginDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebAuthenticationController {
    private final AuthenticationManager authManager;
    private final DrillUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginPage(Model model,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, defaultValue = "/") String redirect) {
        model.addAttribute("errorMessage", error);
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/log_in")
    public ResponseEntity<String> authenticate(HttpServletResponse response,
            @ModelAttribute LoginDTO login,
            @RequestParam(required = false, defaultValue = "/") String redirect){
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword())
        );

        if(!authentication.isAuthenticated()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Credentials");
        }

        String jwtToken = jwtService.generateToken(
            userDetailsService.loadUserByUsername(login.getUsername())
        );

        // set accessToken to cookie header
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(Constants.isProductionServer())
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtService.getMillisValid(
                    jwtService.extractRoles(jwtToken)
                ))
                .build();

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, redirect)
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .build();
    }

    @GetMapping("/log_out")
    public String logoutPage(HttpServletResponse response) {
        // Just clear out and expire the cookie
        ResponseCookie clearCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(Constants.isProductionServer())
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return "logout";
    }
}
