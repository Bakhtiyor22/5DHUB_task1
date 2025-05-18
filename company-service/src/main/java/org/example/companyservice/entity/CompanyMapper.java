package org.example.companyservice.entity;

import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CompanyMapper {

    public Company toCompany(CompanyRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        Company company = new Company();
        company.setName(requestDto.getName());
        company.setBudget(requestDto.getBudget());
        company.setEmployeeIds(requestDto.getEmployeeIds() != null ? requestDto.getEmployeeIds() : Collections.emptyList());
        return company;
    }

    public CompanyResponseDto toCompanyResponseDto(Company company, List<UserDto> employees) {
        if (company == null) {
            return null;
        }
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getBudget(),
                employees
        );
    }
}