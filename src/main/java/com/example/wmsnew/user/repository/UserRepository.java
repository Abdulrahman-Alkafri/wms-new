package com.example.wmsnew.user.repository;

import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Long countByRole(UserRole role);
    Long countByRoleAndIsActiveTrue(UserRole role);
    java.util.List<User> findByRole(UserRole role);
    
    // Additional analytics queries for productivity calculations
    @org.springframework.data.jpa.repository.Query("SELECT u.role, COUNT(u) as totalCount, " +
           "SUM(CASE WHEN u.isActive = true THEN 1 ELSE 0 END) as activeCount " +
           "FROM User u " +
           "GROUP BY u.role")
    java.util.List<Object[]> getUserStatsByRole();
}
