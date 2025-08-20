package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocationRepository
    extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {}
