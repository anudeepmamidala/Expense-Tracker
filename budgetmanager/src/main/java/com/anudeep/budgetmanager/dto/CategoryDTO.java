package com.anudeep.budgetmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    
    private Long id;
    private Long profileId;
    private String name;
    private String icon;
    private String type;
    private String createdAt;
    private String updatedAt;
    // Getters and Setters
}
