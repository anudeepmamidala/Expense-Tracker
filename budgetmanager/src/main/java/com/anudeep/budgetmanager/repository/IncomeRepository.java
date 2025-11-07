package com.anudeep.budgetmanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.anudeep.budgetmanager.entity.IncomeEntity;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // SQL: SELECT * FROM incomes WHERE profile_id = :profileId ORDER BY date DESC
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // SQL: SELECT * FROM incomes WHERE profile_id = :profileId (top 5 by date)
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);
    
    // Total (sum) of amount for a given profileId - FIXED: was querying ExpenseEntity
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id=:profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);
    
    // SQL: SELECT * FROM incomes WHERE profile_id = :profileId AND date BETWEEN :startDate AND :endDate AND name CONTAINS :keyword
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        Long profileId,
        LocalDate startDate,
        LocalDate endDate,
        String keyword,
        Sort sort
    );

    // SQL: SELECT * FROM incomes WHERE profile_id = :profileId AND date BETWEEN :startDate AND :endDate
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}