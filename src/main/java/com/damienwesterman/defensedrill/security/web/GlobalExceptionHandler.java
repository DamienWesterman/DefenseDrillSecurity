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

import java.util.NoSuchElementException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.damienwesterman.defensedrill.security.exception.DatabaseInsertException;

/**
 * TODO: Doc comments
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        // Ex. user provides bad jakarta constraints and fails validation
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(Character.toUpperCase(error.getField().charAt(0)));
            errorMessage.append(error.getField().substring(1));
            errorMessage.append(' ');
            errorMessage.append(error.getDefaultMessage());
            errorMessage.append(". ");
        });

        return ResponseEntity.badRequest()
            .body(errorMessage.toString());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        // Ex. user provides a String for a Long path ID variable
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ex.getLocalizedMessage());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        // Ex. user neglects to provide correct body arguments and fails validation
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A body is required for this request.");
    }

    @ExceptionHandler(DatabaseInsertException.class)
    public ResponseEntity<String> handleDatabaseInsertException(DatabaseInsertException die) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(die.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException nsee) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(nsee.getMessage());
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<String> handleIndexOutOfBoundsException(IndexOutOfBoundsException ioobe) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ioobe.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleLockingFailureException(ObjectOptimisticLockingFailureException oolfe) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("Old data: please refresh and try again");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error has occurred.");
    }
}
