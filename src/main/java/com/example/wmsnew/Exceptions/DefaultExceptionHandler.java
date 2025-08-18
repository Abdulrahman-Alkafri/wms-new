package com.example.wmsnew.Exceptions;

import com.auth0.exception.Auth0Exception;
import com.example.wmsnew.Exceptions.inventoryException.InventoryNotFoundException;
import com.example.wmsnew.Exceptions.orderException.OrderNotFoundException;
import com.example.wmsnew.Exceptions.productExceptions.CategoryNotFoundException;
import com.example.wmsnew.Exceptions.productExceptions.DuplicateProductException;
import com.example.wmsnew.Exceptions.productExceptions.ProductNotFoundException;
import com.example.wmsnew.Exceptions.shipmentExceptions.ShipmentNotFoundException;
import com.example.wmsnew.Exceptions.userExceptions.UserNotFoundException;
import com.example.wmsnew.Exceptions.warehouseExceptions.LocationCapacityExceededException;
import com.example.wmsnew.Exceptions.warehouseExceptions.LocationNotFoundException;
import com.example.wmsnew.Exceptions.warehouseExceptions.WarehouseNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

  private ResponseEntity<ApiError> buildErrorResponse(
      HttpServletRequest request, String message, HttpStatus status, List<String> errors) {
    ApiError apiError =
        new ApiError(request.getRequestURI(), message, status.value(), ZonedDateTime.now(), errors);
    return new ResponseEntity<>(apiError, status);
  }

  // --- Validation ---
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<String> errors =
        ex.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
    return buildErrorResponse(request, "Validation failed", HttpStatus.BAD_REQUEST, errors);
  }

  @ExceptionHandler(LocationCapacityExceededException.class)
  public ResponseEntity<ApiError> handleCapacityExceeded(
      LocationCapacityExceededException ex, HttpServletRequest request) {
    return buildErrorResponse(request, ex.getMessage(), HttpStatus.CONFLICT, List.of());
  }

  // Not found exceptions
  @ExceptionHandler({
    ProductNotFoundException.class,
    CategoryNotFoundException.class,
    InventoryNotFoundException.class,
    OrderNotFoundException.class,
    ShipmentNotFoundException.class,
    UserNotFoundException.class,
    WarehouseNotFoundException.class,
    LocationNotFoundException.class
  })
  public ResponseEntity<ApiError> handleResourceNotFound(
      RuntimeException ex, HttpServletRequest request) {
    return buildErrorResponse(request, ex.getMessage(), HttpStatus.NOT_FOUND, List.of());
  }

  @ExceptionHandler(DuplicateProductException.class)
  public ResponseEntity<ApiError> handleDuplicateProduct(
      DuplicateProductException ex, HttpServletRequest request) {
    return buildErrorResponse(request, ex.getMessage(), HttpStatus.CONFLICT, List.of());
  }

  // --- Client / Auth0 ---
  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ApiError> handleClientError(
      HttpClientErrorException ex, HttpServletRequest request) {
    return buildErrorResponse(request, ex.getMessage(), (HttpStatus) ex.getStatusCode(), List.of());
  }

  @ExceptionHandler(Auth0Exception.class)
  public ResponseEntity<ApiError> handleAuth0Exception(
      Auth0Exception ex, HttpServletRequest request) {
    return buildErrorResponse(
        request,
        "Authentication service error: " + ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        List.of());
  }

  // --- Fallback ---
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
    return buildErrorResponse(
        request,
        "Unexpected error: " + ex.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        List.of());
  }
}
