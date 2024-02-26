package com.example.demo.responses;

import com.example.demo.entities.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private UserType userType;
}