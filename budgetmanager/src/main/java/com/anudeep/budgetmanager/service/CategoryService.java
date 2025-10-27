package com.anudeep.budgetmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.dto.CategoryDTO;
import com.anudeep.budgetmanager.entity.CategoryEntity;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;


    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category with the same name already exists for this profile.");
        }
        CategoryEntity categoryEntity = toEntity(categoryDTO, profile);
        categoryEntity = categoryRepository.save(categoryEntity);

        return toDTO(categoryEntity);
    }



    public List<CategoryDTO> getCategoriesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> categories= categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }


    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> categories=categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDTO).toList();


    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO dto){
        
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity existingcategory=categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                    .orElseThrow(()->new RuntimeException("Category not found"));
        existingcategory.setName(dto.getName());
        existingcategory.setIcon(dto.getIcon());
        existingcategory=categoryRepository.save(existingcategory);
        return toDTO(existingcategory);
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO,ProfileEntity profile) {
        return CategoryEntity.builder()
            .name(categoryDTO.getName())
            .icon(categoryDTO.getIcon())
            .profile(profile)
            .type(categoryDTO.getType())
            .build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
            .id(categoryEntity.getId())
            .profileId(categoryEntity.getProfile().getId())
            .name(categoryEntity.getName())
            .icon(categoryEntity.getIcon())
            .type(categoryEntity.getType())
            .createdAt(categoryEntity.getCreatedAt().toString())
            .updatedAt(categoryEntity.getUpdatedAt().toString())
            .build();
    }
}
