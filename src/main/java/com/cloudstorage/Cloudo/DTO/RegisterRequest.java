package com.cloudstorage.Cloudo.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String name;
}
