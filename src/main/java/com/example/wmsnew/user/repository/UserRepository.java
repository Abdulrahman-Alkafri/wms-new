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
}
