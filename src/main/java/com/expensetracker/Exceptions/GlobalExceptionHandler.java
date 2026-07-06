package com.expensetracker.Exceptions;

import com.expensetracker.Helpers.ApiResponse;
import com.expensetracker.Helpers.ExceptionBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException e){
        ApiResponse response = new ApiResponse(e.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e){
        ApiResponse response = null;
        if (e instanceof NoResourceFoundException) {
            response = new ApiResponse("Resource not found in this path: '/" + ((NoResourceFoundException) e).getResourcePath() + "'", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response = new ApiResponse(e.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionBody> handleApiException(ApiException e){
        ExceptionBody exceptionResponse = new ExceptionBody(e.getHttpStatus().value(), e.getMessage());
        return new ResponseEntity<>(exceptionResponse, e.getHttpStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionBody> handleBadCredentialException(BadCredentialsException e){
        ExceptionBody exceptionResponse = new ExceptionBody(401, "Unauthorized");
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
}
