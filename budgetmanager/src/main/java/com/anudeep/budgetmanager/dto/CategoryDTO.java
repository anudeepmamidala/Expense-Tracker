package com.anudeep.budgetmanager.dto;

import lombok.Data;

@Data

public class CategoryDTO {

    
    private Long id;
    private String name;
    private String type;
    private String createdAt;
    private String updatedAt;
    // Getters and Setters
}
