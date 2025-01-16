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

package com.damienwesterman.defensedrill.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillSecurityApplication {
	// TODO: TDD the RestAPI layer (verification/validation)
	// TODO: controllers for user endpoints (TDD on this one too, especially for create etc)
	// TODO: create users lists and things endoints
	// TODO: make sure to make the global exception handler (check every time of exception we throw, plus those handled in mvc) - should maybe return /login?error=true
	// TODO: Double check all endpoints and their access
	// TODO: Address all startup warnings

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillSecurityApplication.class, args);
	}

}
