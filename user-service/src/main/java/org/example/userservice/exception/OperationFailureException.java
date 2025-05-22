package org.example.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OperationFailureException extends RuntimeException {
    public OperationFailureException(String message) {
        super(message);
    }

    public OperationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
