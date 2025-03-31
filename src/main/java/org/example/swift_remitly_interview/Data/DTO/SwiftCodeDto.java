package org.example.swift_remitly_interview.Data.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeDto {
    @NotBlank(message = "Address cannot be empty")
    String address;

    @NotBlank(message = "Bank name cannot be empty")
    String bankName;

    @Size(min = 2, max = 2, message = "Country ISO2 code must be exactly 2 characters long")
    String countryISO2;

    @NotBlank(message = "Country name cannot be empty")
    String countryName;

    @NotNull(message = "Headquarter status cannot be null")
    Boolean isHeadquarter;

    @Size(min = 8, max = 11, message = "SWIFT code must be either 8 or 11 characters long")
    String swiftCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<BranchSwiftCodeDto> branches;
}
