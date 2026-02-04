package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * User Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer userId;
    private String username;
    private String email;
    private String phone;
    private String address;
    private Date createdDate;
}
