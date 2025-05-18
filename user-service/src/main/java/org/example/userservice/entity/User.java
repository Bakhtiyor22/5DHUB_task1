package org.example.userservice.entity;

import lombok.Data; // Lombok annotation for getters, setters, toString, etc.
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class User {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Long companyId;
}