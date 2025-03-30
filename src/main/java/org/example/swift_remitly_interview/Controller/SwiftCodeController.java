package org.example.swift_remitly_interview.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.example.swift_remitly_interview.Data.DTO.CountryISODto;
import org.example.swift_remitly_interview.Data.DTO.MessageResponse;
import org.example.swift_remitly_interview.Data.DTO.SwiftCodeDto;
import org.example.swift_remitly_interview.Service.SwiftCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(value = "/v1/swift-codes")
public class SwiftCodeController {
    private final SwiftCodeService swiftCodeService;

    @GetMapping(value = "/{swift_code}")
    public ResponseEntity<SwiftCodeDto> getSwiftCode(@PathVariable @NotBlank String swift_code) {
        SwiftCodeDto swiftCodeDTO = swiftCodeService.getSwiftCode(swift_code);
        return ResponseEntity.status(HttpStatus.OK).body(swiftCodeDTO);
    }

    @GetMapping(value = "/country/{countryISO2code}")
    public ResponseEntity<CountryISODto> getSwiftCodeByCountryISO2(@PathVariable @NotBlank String countryISO2code) {
        CountryISODto countryISODto = swiftCodeService.getSwiftCodeByCountryISO2(countryISO2code);
        return ResponseEntity.status(HttpStatus.OK).body(countryISODto);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createSwiftCode(@Valid @RequestBody SwiftCodeDto swiftCodeDto) {
        MessageResponse message = swiftCodeService.createSwiftCode(swiftCodeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @DeleteMapping("/{swift_code}")
    public ResponseEntity<MessageResponse> deleteSwiftCode(@PathVariable @NotBlank String swift_code) {
        MessageResponse message = swiftCodeService.deleteSwiftCode(swift_code);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
