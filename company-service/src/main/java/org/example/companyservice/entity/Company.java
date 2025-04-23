package org.example.companyservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList; // Use interface List
import java.util.List;

@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Double budget;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_employee_ids", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "employee_id")
    private List<Long> employeeIds = new ArrayList<>();
}