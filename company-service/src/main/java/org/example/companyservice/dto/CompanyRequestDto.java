package org.example.companyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDto {
    private String name;
    private Double budget;
    private List<Long> employeeIds; // Or List<UserDto> if you prefer to send full user objects in request
}
