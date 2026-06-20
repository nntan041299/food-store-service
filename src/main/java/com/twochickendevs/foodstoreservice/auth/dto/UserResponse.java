package com.twochickendevs.foodstoreservice.auth.dto;

import com.twochickendevs.foodstoreservice.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private boolean active;
}
