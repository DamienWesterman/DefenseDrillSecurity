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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.damienwesterman.defensedrill.security.web.dto.UserFormDTO;
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
                entityToFormDto(user, PASSWORD),
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
        assertEquals(1, repo.findAll().size());
        assertEquals(USERNAME, repo.findAll().get(0).getName());
    }

    @Test
    public void test_createEndpoint_encodesPassword() {
        ResponseEntity<UserInfoDTO> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT),
                entityToFormDto(user, PASSWORD),
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotEquals(PASSWORD, repo.findAll().get(0).getPassword());
        assertTrue(passwordEncoder.matches(PASSWORD, repo.findAll().get(0).getPassword()));
    }

    @Test
    public void test_create_fails_withDuplicateName() {
        repo.save(user);

        ResponseEntity<String> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT),
                entityToFormDto(user, PASSWORD),
                String.class
            );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_create_fails_withInvalidRole() {
        user.setRoles("Invalid");

        ResponseEntity<String> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT),
                entityToFormDto(user, PASSWORD),
                String.class
            );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        user.setRoles(UserRoles.USER.getStringRepresentation());
        repo.save(user);

        UserEntity user2 = UserEntity.builder()
            .name("Name 2")
            .password(passwordEncoder.encode("Password 2"))
            .roles(UserRoles.ADMIN.getStringRepresentation())
            .build();
        repo.save(user2);

        ResponseEntity<UserInfoDTO[]> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT + "/roles/" + UserRoles.USER.getStringRepresentation()),
                UserInfoDTO[].class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals(USERNAME, response.getBody()[0].getUsername());
    }

    @Test
    public void test_findAllByRole_returnsNoContent_withNoMatchingRoles() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        repo.save(user);

        ResponseEntity<UserInfoDTO[]> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT + "/roles/" + UserRoles.ADMIN.getStringRepresentation()),
                UserInfoDTO[].class
            );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void test_findAllByRole_returnsNoContent_withNonExistentRole() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        repo.save(user);

        ResponseEntity<UserInfoDTO[]> response =
            restTemplate.getForEntity(
                URI.create(UsersController.ENDPOINT + "/roles/INVALID"),
                UserInfoDTO[].class
            );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
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

    @Test
    public void test_update_changesName_whenGivenProperInput() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        Long userId = repo.save(user).getId();

        assertEquals(1, repo.findAll().size());
        assertEquals(USERNAME, repo.findAll().get(0).getName());

        UserFormDTO updateUser = entityToFormDto(user, PASSWORD);
        String newName = "New User Name";
        updateUser.setUsername(newName);

        ResponseEntity<UserInfoDTO> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + userId),
                updateUser,
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newName, response.getBody().getUsername());
        assertEquals(1, repo.findAll().size());
        assertEquals(newName, repo.findAll().get(0).getName());
    }

    @Test
    public void test_update_encodesPassword() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        Long userId = repo.save(user).getId();

        UserFormDTO updateUser = entityToFormDto(user, PASSWORD);
        String newPassword = "New Password";
        updateUser.setPassword(newPassword);

        ResponseEntity<UserInfoDTO> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + userId),
                updateUser,
                UserInfoDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotEquals(newPassword, repo.findAll().get(0).getPassword());
        assertTrue(passwordEncoder.matches(newPassword, repo.findAll().get(0).getPassword()));
    }

    @Test
    public void test_update_fails404_withNonexistentId() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        Long userId = repo.save(user).getId();

        assertEquals(1, repo.findAll().size());
        assertEquals(USERNAME, repo.findAll().get(0).getName());

        UserFormDTO updateUser = entityToFormDto(user, PASSWORD);
        String newName = "New User Name";
        updateUser.setUsername(newName);

        ResponseEntity<String> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + (userId + 1)),
                updateUser,
                String.class
            );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, repo.findAll().size());
        assertEquals(USERNAME, repo.findAll().get(0).getName());
    }

    @Test
    public void test_update_fails_withDuplicateName() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        repo.save(user);

        String nonDuplicateName = "Name 2";
        UserEntity user2 = UserEntity.builder()
            .name(nonDuplicateName)
            .password(passwordEncoder.encode("Password 2"))
            .roles(UserRoles.ADMIN.getStringRepresentation())
            .build();
        Long userId = repo.save(user2).getId();

        assertEquals(2, repo.findAll().size());
        assertEquals(nonDuplicateName, repo.findById(userId).get().getName());

        UserFormDTO updateUser = entityToFormDto(user2, PASSWORD);
        updateUser.setUsername(USERNAME);

        ResponseEntity<String> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + userId ),
                updateUser,
                String.class
            );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, repo.findAll().size());
        assertEquals(nonDuplicateName, repo.findById(userId).get().getName());
    }

    @Test
    public void test_udpate_fails_withInvalidRole() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        Long userId = repo.save(user).getId();

        UserFormDTO updateUser = entityToFormDto(user, PASSWORD);
        updateUser.setRoles(List.of("INVALID"));

        ResponseEntity<String> response =
            restTemplate.postForEntity(
                URI.create(UsersController.ENDPOINT + "/id/" + userId),
                updateUser,
                String.class
            );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_delete_succeeds() {
        user.setRoles(UserRoles.USER.getStringRepresentation());
        Long userId = repo.save(user).getId();
        assertEquals(1, repo.findAll().size());

        restTemplate.delete(
            URI.create(UsersController.ENDPOINT + "/id/" + userId)
        );

        assertEquals(0, repo.findAll().size());
    }

    private UserFormDTO entityToFormDto(UserEntity entity, String unencryptedPassword) {
        UserFormDTO ret = new UserFormDTO();
        ret.setUsername(entity.getName());
        ret.setPassword(unencryptedPassword);
        ret.setRoles(List.of(entity.getRoles().split(",")));

        return ret;
    }
}
