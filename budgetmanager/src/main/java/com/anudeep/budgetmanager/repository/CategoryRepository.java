package com.anudeep.budgetmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anudeep.budgetmanager.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    // select * from categories where profile_id = ?
    List<CategoryEntity> findByProfileId(Long profileId);

    // select * from categories where id = ? and profile_id = ?
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long profileId);
}
