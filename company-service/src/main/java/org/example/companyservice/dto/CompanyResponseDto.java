package org.example.companyservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {
    private Long id;
    private String name;
    private Double budget;
    private List<UserDto> employees;
}
