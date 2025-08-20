package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardSizeResponse {
    private Long id;
    private Integer width;
    private Integer height;
    private Integer depth;
    private Integer volume;
    private String sizeName;
}