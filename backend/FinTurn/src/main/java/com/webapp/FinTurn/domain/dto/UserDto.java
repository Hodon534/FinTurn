package com.webapp.FinTurn.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    /**
     * Hide id in the backend
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;
    private String userId;
    private String username;
    /**
     * Hide password in the backend
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
}
