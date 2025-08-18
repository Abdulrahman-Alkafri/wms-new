package com.example.wmsnew.Exceptions;

import java.time.ZonedDateTime;
import java.util.List;

public record ApiError(
    String path, String message, int statusCode, ZonedDateTime timestamp, List<String> errors) {}
