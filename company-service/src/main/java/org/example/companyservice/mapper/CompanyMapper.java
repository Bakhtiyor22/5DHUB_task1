package org.example.companyservice.mapper;

import org.example.companyservice.dto.CompanyCreateRequestDto;
import org.example.companyservice.dto.CompanyDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.UserDto;
import org.example.companyservice.entity.Company;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CompanyMapper {

    public Company toCompany(CompanyCreateRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        Company company = new Company();
        company.setName(requestDto.getName());
        company.setBudget(requestDto.getBudget());
        company.setEmployeeIds(requestDto.getEmployeeIds() != null ?
                requestDto.getEmployeeIds() : Collections.emptyList());
        return company;
    }

    public CompanyDto toSimpleCompanyDto(Company company) {
        if (company == null) {
            return null;
        }
        return new CompanyDto(company.getId(), company.getName(), company.getBudget());
    }

    public CompanyResponseDto toCompanyResponseDto(Company company, List<UserDto> employees) {
        if (company == null) {
            return null;
        }
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getBudget(),
                employees != null ? employees : Collections.emptyList()
        );
    }
}