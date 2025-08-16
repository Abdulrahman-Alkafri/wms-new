package com.example.wmsnew.user.repository;

import com.example.wmsnew.common.enums.UserRole;
import com.example.wmsnew.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByAuth0Id(String auth0Id);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByWarehouseId(Long warehouseId);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByWarehouseIdAndRole(Long warehouseId, UserRole role);
}