package com.example.demo.repository;

import com.example.demo.entity.RoleConflict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleConflictRepository extends JpaRepository<RoleConflict, Long> {
    boolean existsByRoleOneIdAndRoleTwoId(Long roleOneId, Long roleTwoId);

    Optional<RoleConflict> findByRoleOneIdAndRoleTwoId(Long roleOneId, Long roleTwoId);

    List<RoleConflict> findByRoleOneIdOrRoleTwoId(Long roleOneId, Long roleTwoId);
}