package org.example.companyservice.mapper;

import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.UserDto;
import org.example.companyservice.entity.Company;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {

    public Company toCompany(CompanyRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        Company company = new Company();
        company.setName(requestDto.getName());
        company.setBudget(requestDto.getBudget());
        company.setEmployeeIds(Collections.emptyList());
        return company;
    }

    public CompanyResponseDto toCompanyResponseDto(Company company, List<Optional<UserDto>> employees) {
        if (company == null) {
            return null;
        }
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getBudget(),
                employees.stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }
}