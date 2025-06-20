package by.evgen.motor_depot.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "NOT_FOUND",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException ex) {
        String message = ex.getMessage();
        if (message != null && message.startsWith("ERROR: APP:")) {
            message = message.substring("ERROR: APP:".length()).split("\\n")[0].trim();
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "APP_ERROR",
                            message,
                            LocalDateTime.now()
                    ));
        }

        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        message,
                        LocalDateTime.now()
                ));
    }
}
