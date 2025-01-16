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

package com.damienwesterman.defensedrill.security.endToEnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.damienwesterman.defensedrill.security.entity.UserEntity;
import com.damienwesterman.defensedrill.security.repository.UserRepository;
import com.damienwesterman.defensedrill.security.util.Constants.UserRoles;
import com.damienwesterman.defensedrill.security.web.UsersController;
import com.damienwesterman.defensedrill.security.web.dto.UserCreateDTO;
import com.damienwesterman.defensedrill.security.web.dto.UserInfoDTO;

@SuppressWarnings("null")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerTest {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    UserRepository repo;
    @Autowired
    PasswordEncoder passwordEncoder;

    UserEntity user;

    final String USERNAME = "Username 1";
    final String PASSWORD = "Password 1";

    @BeforeEach
    public void setup() {
        repo.deleteAll();

        user = UserEntity.builder()
            .id(null)
            .name(USERNAME)
            .password(passwordEncoder.encode(PASSWORD))
            .roles("")
            .build();
    }

    @Test
    public void test_create_succeeds_withCorrectFields() {
        user.setRoles(UserRoles.USER.getStringRepresentation());

        ResponseEntity<UserInfoDTO> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT),
                entityToCreateDto(user, PASSWORD),
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
        assertEquals(1, repo.findAll().size());
        assertEquals(USERNAME, repo.findAll().get(0).getName());
    }

    @Test
    public void test_createEndpoint_encodesPassword() {
        fail();
    }

    @Test
    public void test_create_fails_withDuplicateName() {
        fail();
    }

    @Test
    public void test_create_fails_withInvalidRole() {
        fail();
    }

    @Test
    public void test_findAll_returnsNoContent_withEmptyDatabase() {
        ResponseEntity<UserInfoDTO[]> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT),
                UserInfoDTO[].class
            );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void test_findAll_returnsSuccessfully_withOneUserInDatabase() {
        repo.save(user);

        ResponseEntity<UserInfoDTO[]> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT),
                UserInfoDTO[].class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals(USERNAME, response.getBody()[0].getUsername());
    }

    @Test
    public void test_findAllByRole_returnsSuccessfully_withMatchingRoles() {
        fail();
    }

    @Test
    public void test_findAllByRole_returnsNoContent_withNoMatchingRoles() {
        fail();
    }

    @Test
    public void test_findAllByRole_returnsNoContent_withNonExistentRole() {
        fail();
    }

    @Test
    public void test_findById_returnsSuccessfully_withExistingId() {
        Long userId = repo.save(user).getId();

        ResponseEntity<UserInfoDTO> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + userId),
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
    }

    @Test
    public void test_findById_fails404_withNonExistingId() {
        ResponseEntity<UserInfoDTO> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + 1L),
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /*
     * TODO:
     * update - succeeds (changes name)
     * update - encodes password
     * update - fails (no name/pass)
     * update - fails (duplicate name)
     * update - fails (invalid role)
     * delete - succeeds
     */

    private UserCreateDTO entityToCreateDto(UserEntity entity, String unencryptedPassword) {
        UserCreateDTO ret = new UserCreateDTO();
        ret.setUsername(entity.getName());
        ret.setPassword(unencryptedPassword);
        ret.setRoles(List.of(entity.getRoles().split(",")));

        return ret;
    }
}
