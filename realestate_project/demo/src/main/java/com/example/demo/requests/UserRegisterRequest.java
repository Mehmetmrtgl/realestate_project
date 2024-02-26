package com.example.demo.requests;

import com.example.demo.entities.UserType;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterRequest {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String mobileNumber;

    private UserType userType;
}
