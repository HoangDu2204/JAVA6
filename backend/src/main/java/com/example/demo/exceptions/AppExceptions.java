package com.example.demo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppExceptions {

    public static class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(String message) {
            super(message);
        }
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message) {
            super(message);
        }

        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateCategoryException extends RuntimeException {
        public DuplicateCategoryException(String message) {
            super(message);
        }
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    public static class ServerErrorException extends RuntimeException {
        public ServerErrorException(String message) {
            super(message);
        }

        public ServerErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    @Getter
    public static class ValidationException extends RuntimeException {
        private final String field;

        public ValidationException(String message, String field) {
            super(message);
            this.field = field;
        }
    }
}
