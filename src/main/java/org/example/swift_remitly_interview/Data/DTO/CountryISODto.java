package org.example.swift_remitly_interview.Data.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryISODto {
    @NotBlank(message = "Country ISO2 code cannot be empty")
    @Size(min = 2, max = 2, message = "Country ISO2 code must be exactly 2 characters long")
    String countryISO2;

    @NotBlank(message = "Country name cannot be empty")
    String countryName;

    List<BranchSwiftCodeDto> swiftCodes;
}
