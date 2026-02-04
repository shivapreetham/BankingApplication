package com.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Account Request DTO for creating/updating accounts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank(message = "Account type is required")
    private String accountType;

    @DecimalMin(value = "0.01", message = "Initial balance must be at least 0.01")
    private BigDecimal initialBalance;
}
