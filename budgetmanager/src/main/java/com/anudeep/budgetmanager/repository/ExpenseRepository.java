package com.anudeep.budgetmanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.anudeep.budgetmanager.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // SQL: SELECT * FROM expenses WHERE profile_id = :profileId ORDER BY date DESC
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // SQL: SELECT * FROM expenses WHERE profile_id = :profileId (top 5 by date)
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);
    
    // Total (sum) of amount for a given profileId
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id=:profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
    
    // SQL: SELECT * FROM expenses WHERE profile_id = :profileId AND date BETWEEN :startDate AND :endDate AND name CONTAINS :keyword
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        Long profileId,
        LocalDate startDate,
        LocalDate endDate,
        String keyword,
        Sort sort
    );

    // SQL: SELECT * FROM expenses WHERE profile_id = :profileId AND date BETWEEN :startDate AND :endDate
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    // SQL: SELECT * FROM expenses WHERE profile_id = :profileId AND date = :date
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}