package com.school.library.auth.dto;

import lombok.*;

@Data
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String address;    
    private String password;
}
