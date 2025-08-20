package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardSizeRequest {
    private Integer width;
    private Integer height;
    private Integer depth;
    private Integer volume;
}