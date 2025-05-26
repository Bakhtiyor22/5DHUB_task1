package org.example.userservice.company;

import org.example.userservice.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/api/v1/companies/simple/{id}")
    CompanyDto getCompanyById(@PathVariable("id") Long id);
}