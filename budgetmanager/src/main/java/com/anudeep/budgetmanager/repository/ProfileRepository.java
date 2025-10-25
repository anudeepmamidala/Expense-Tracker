package com.anudeep.budgetmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anudeep.budgetmanager.entity.ProfileEntity;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {   


    //SELECT * FROM profiles WHERE email = ?
    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByActivationToken(String token);
}