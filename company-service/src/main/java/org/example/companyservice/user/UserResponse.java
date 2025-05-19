package org.example.companyservice.user;

public record UserResponse(
        String id,
        String name,
        String email,
        String phone,
        String address
) {

}
