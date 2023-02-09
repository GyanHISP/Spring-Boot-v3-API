package com.wowApp.payload.response;

import com.wowApp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    private String token;

}
