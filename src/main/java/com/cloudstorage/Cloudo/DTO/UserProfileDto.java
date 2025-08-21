package com.cloudstorage.Cloudo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private String email;
    private String name;
    private String role;
}
