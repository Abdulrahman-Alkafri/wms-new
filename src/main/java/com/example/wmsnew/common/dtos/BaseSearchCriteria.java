package com.example.wmsnew.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseSearchCriteria {
  @Builder.Default private Integer page = 1;
  @Builder.Default private Integer size = 10;
  @Builder.Default private String sortBy = "id";
  @Builder.Default private String sortDir = "asc";
}
