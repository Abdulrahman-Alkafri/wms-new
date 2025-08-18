package com.example.wmsnew.warehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "standard_sizes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardSizes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer width;

  private Integer height;

  private Integer depth;

  private Integer volume;

  /** Generates a readable name for the bin size Example: "W50xH30xD20_V30kg_MW100kg" */
  public String getSizeName() {
    StringBuilder sb = new StringBuilder();
    if (width != null) sb.append("W").append(width);
    if (height != null) sb.append("xH").append(height);
    if (depth != null) sb.append("xD").append(depth);
    if (volume != null) sb.append("_V").append(volume).append("L");
    return sb.toString();
  }
}
