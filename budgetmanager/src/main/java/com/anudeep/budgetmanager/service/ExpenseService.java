package com.anudeep.budgetmanager.service;

import java.time.LocalDate;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.dto.ExpenseDTO;
import com.anudeep.budgetmanager.dto.IncomeDTO;
import com.anudeep.budgetmanager.entity.CategoryEntity;
import com.anudeep.budgetmanager.entity.ExpenseEntity;
import com.anudeep.budgetmanager.entity.IncomeEntity;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.CategoryRepository;
import com.anudeep.budgetmanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    private final ProfileService profileService;



    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId()).orElseThrow(
            ()->new RuntimeException("Category not found")
        );
        ExpenseEntity newExpense = toEntity(dto, profile, category);
        newExpense=expenseRepository.save(newExpense);
        return toDTO(newExpense);

    }



    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();

        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }


    public void deleteExpense(Long incomeId){
        ProfileEntity profile= profileService.getCurrentProfile();
        ExpenseEntity entity=expenseRepository.findById(incomeId)
        .orElseThrow(()-> new RuntimeException("Expense not found"));

        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        expenseRepository.delete(entity);
    }

    public ExpenseEntity toEntity(ExpenseDTO dto,ProfileEntity profile,CategoryEntity category){

        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public ExpenseDTO toDTO(ExpenseEntity entity){

        return ExpenseDTO.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .icon(entity.getIcon())
                    .amount(entity.getAmount())
                    .categoryId(entity.getCategory()!=null ? entity.getCategory().getId():null)
                    .categoryName(entity.getCategory()!=null?entity.getCategory().getName():"N/A")
                    .amount(entity.getAmount())
                    .date(entity.getDate())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();


    }
}
