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

public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long>{
    

    Optional<ExpenseEntity> findById(Long id);


    // Optional<ExpenseEntity> findByIdAndUserId(Long id, Long userId);
    // SQL: SELECT * FROM expense_entity i
    //      WHERE i.profile_id = :profileId
    //      ORDER BY i.date DESC
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // SQL: SELECT * FROM expense_entity i
    //      WHERE i.profile_id = :profileId
    //        AND i.date BETWEEN :startDate AND :endDate
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);
    
    // Total (sum) of amount for a given profileId
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id=:profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
    
    // List<ExpenseEntity> findByProfileIdAndDateBetweenContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate,String keyword,Sort sort);
    // Corrected method name
List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
    Long profileId,
    LocalDate startDate,
    LocalDate endDate,
    String keyword, // Renamed for clarity
    Sort sort
);
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId,LocalDate startDate,LocalDate endDate);
}
