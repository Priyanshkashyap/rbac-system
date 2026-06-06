package com.example.demo.repository;

import com.example.demo.entity.RoleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleGroupRepository
        extends JpaRepository<RoleGroup, Long> {
}