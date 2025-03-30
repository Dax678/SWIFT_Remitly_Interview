package org.example.swift_remitly_interview.Data.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BranchSwiftCodeDto {
    String address;

    @NotBlank(message = "Bank name cannot be empty")
    String bankName;

    @NotBlank(message = "Country ISO2 code cannot be empty")
    @Size(min = 2, max = 2, message = "Country ISO2 code must be exactly 2 characters long")
    String countryISO2;

    @NotNull(message = "Headquarter status cannot be null")
    Boolean isHeadquarter;

    @NotBlank(message = "SWIFT code cannot be empty")
    @Size(min = 8, max = 11, message = "SWIFT code must be either 8 or 11 characters long")
    String swiftCode;
}
