package org.example.companyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequestDto {

    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String name;

    @Positive(message = "Budget must be positive")
    private Double budget;

    private List<String> employeeIds;
}