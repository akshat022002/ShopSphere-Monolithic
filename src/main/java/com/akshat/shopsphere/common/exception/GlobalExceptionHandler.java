package com.akshat.shopsphere.common.exception;

import com.akshat.shopsphere.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex){

        ApiResponse<Void> response = new ApiResponse<>(false,
                                                                ex.getMessage(),
                                                           null,
                                                                LocalDateTime.now()
                );

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);

    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex){

        ApiResponse<Void> response = new ApiResponse<>(false,
                                                                ex.getMessage(),
                                                           null,
                                                                LocalDateTime.now()
                                                        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);

    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex){

        ApiResponse<Void> response = new ApiResponse<>(false,
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex){

        ApiResponse<Void> response = new ApiResponse<>(false
                ,ex.getMessage()
                ,null
                ,LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex){

     String message = ex.getBindingResult()
             .getFieldErrors()
             .stream()
             .map(error->
                                    error.getField()+" : "+error.getDefaultMessage() )
             .collect(Collectors.joining(","));

        ApiResponse<Void> response = new ApiResponse<>(false,message,null,LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex){

        log.error("Unexpected error occurred ",ex);

        ApiResponse<Void> response = new ApiResponse<>(false
                ,"An unexpected error occurred"
                ,null
                , LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
