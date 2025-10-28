package com.anudeep.budgetmanager.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.dto.ExpenseDTO;
import com.anudeep.budgetmanager.dto.IncomeDTO;
import com.anudeep.budgetmanager.entity.CategoryEntity;
import com.anudeep.budgetmanager.entity.ExpenseEntity;
import com.anudeep.budgetmanager.entity.IncomeEntity;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.CategoryRepository;
import com.anudeep.budgetmanager.repository.ExpenseRepository;
import com.anudeep.budgetmanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {
    

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;

    private final ProfileService profileService;


    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId()).orElseThrow(
            ()->new RuntimeException("Category not found")
        );
        IncomeEntity newIncome = toEntity(dto, profile, category);
        newIncome=incomeRepository.save(newIncome);
        return toDTO(newIncome);

    }



    public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();

        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }


    public void deleteExpense(Long incomeId){
        ProfileEntity profile= profileService.getCurrentProfile();
        IncomeEntity entity=incomeRepository.findById(incomeId)
        .orElseThrow(()-> new RuntimeException("Expense not found"));

        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(entity);
    }


    public IncomeEntity toEntity(IncomeDTO dto,ProfileEntity profile,CategoryEntity category){

        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public IncomeDTO toDTO(IncomeEntity entity){

        return IncomeDTO.builder()
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
