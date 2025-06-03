package org.example.companyservice.repository;

import org.example.companyservice.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);

    @Query("SELECT c FROM Company c WHERE :userId MEMBER OF c.employeeIds")
    Optional<Company> findByEmployeeId(@Param("userId") String userId);
}
