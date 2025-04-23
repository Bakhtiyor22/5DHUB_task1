package org.example.userservice.entity;

import jakarta.persistence.*;
import lombok.Data; // Lombok annotation for getters, setters, toString, etc.
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users") // "user" is often a reserved keyword in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private Long companyId;
}