package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRequest {
  private String warehouseName;
  private List<LocationGroupRequest> locationGroups;
}

// {
//        "warehouseName": "Central Warehouse",
//        "locationGroups": [
//        {
//        "standardSizeId": 1,
//        "rows": 2,
//        "racksPerRow": 2,
//        "shelvesPerRack": 2,
//        "binsPerShelf": 2
//        },
//        {
//        "standardSizeId": 2,
//        "rows": 1,
//        "racksPerRow": 2,
//        "shelvesPerRack": 1,
//        "binsPerShelf": 3
//        }
//        ]
//        }


// and the  it will be sttored in the db like this
//[
//        {
//        "locationCode": "R1-RK1-S1-B1",
//        "asile": "A1",
//        "rack": "R1",
//        "shelf": "S1",
//        "bin": "B1",
//        "standardSizeId": 1
//        },
//        {
//        "locationCode": "R1-RK1-S1-B2",
//        "asile": "A1",
//        "rack": "R1",
//        "shelf": "S1",
//        "bin": "B2",
//        "standardSizeId": 1
//        },
//        {
//        "locationCode": "R2-RK2-S2-B2",
//        "asile": "A2",
//        "rack": "R2",
//        "shelf": "S2",
//        "bin": "B2",
//        "standardSizeId": 1
//        },
//        {
//        "locationCode": "R1-RK2-S1-B3",
//        "asile": "A1",
//        "rack": "R2",
//        "shelf": "S1",
//        "bin": "B3",
//        "standardSizeId": 2
//        }
//        ]
