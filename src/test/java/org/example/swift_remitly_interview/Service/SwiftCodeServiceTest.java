package org.example.swift_remitly_interview.Service;

import org.example.swift_remitly_interview.Configuration.Exception.CountryISOCodeNotFoundException;
import org.example.swift_remitly_interview.Configuration.Exception.SwiftCodeAlreadyExistsException;
import org.example.swift_remitly_interview.Configuration.Exception.SwiftCodeNotFoundException;
import org.example.swift_remitly_interview.Data.DTO.CountryISODto;
import org.example.swift_remitly_interview.Data.DTO.Mapper.SwiftCodeMapperImpl;
import org.example.swift_remitly_interview.Data.DTO.MessageResponse;
import org.example.swift_remitly_interview.Data.DTO.SwiftCodeDto;
import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.example.swift_remitly_interview.Repository.SwiftCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwiftCodeServiceTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private SwiftCodeMapperImpl swiftCodeMapper;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    private SwiftCode swiftCode;
    private SwiftCodeDto swiftCodeDto;
    private CountryISODto countryISODto;

    @BeforeEach
    void setUp() {
        swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFXXX");
        swiftCode.setCountryISO2("XX");
        swiftCode.setBankName("Test Bank");

        swiftCodeDto = new SwiftCodeDto();
        swiftCodeDto.setSwiftCode("ABCDEFXXX");
        swiftCodeDto.setCountryISO2("XX");
        swiftCodeDto.setBankName("Test Bank");

        countryISODto = new CountryISODto();
        countryISODto.setCountryISO2("XX");
        countryISODto.setCountryName("Test Country");

    }

    @Test
    @DisplayName("Should return SwiftCodeDto when the SWIFT code exists")
    void shouldReturnSwiftCodeDto_whenSwiftCodeExists() {
        // Given
        when(swiftCodeRepository.findBySwiftCode("ABCDEFXXX")).thenReturn(Optional.of(swiftCode));
        when(swiftCodeMapper.toSwiftCodeDto(swiftCode)).thenReturn(swiftCodeDto);

        // When
        SwiftCodeDto result = swiftCodeService.getSwiftCode("ABCDEFXXX");

        // Then
        assertNotNull(result);
        assertEquals(swiftCodeDto.getSwiftCode(), result.getSwiftCode());
        assertEquals(swiftCodeDto.getBankName(), result.getBankName());
    }

    @Test
    @DisplayName("Should throw an exception when the SWIFT code is not found")
    void shouldThrowException_whenSwiftCodeNotFound() {
        // Given
        when(swiftCodeRepository.findBySwiftCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        SwiftCodeNotFoundException exception = assertThrows(SwiftCodeNotFoundException.class,
                () -> swiftCodeService.getSwiftCode("INVALID"));

        assertEquals("SWIFT code: INVALID not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return CountryISODto when the SWIFT code exists for a given country")
    void shouldReturnCountryISODto_whenSwiftCodeExists() {
        // Given
        when(swiftCodeRepository.findByCountryISO2("XX")).thenReturn(List.of(swiftCode));
        when(swiftCodeMapper.toCountryISODto(List.of(swiftCode))).thenReturn(countryISODto);

        // When
        CountryISODto result = swiftCodeService.getSwiftCodeByCountryISO2("XX");

        // Then
        assertNotNull(result);
        assertEquals(countryISODto.getCountryISO2(), result.getCountryISO2());
        assertEquals(countryISODto.getCountryName(), result.getCountryName());
    }

    @Test
    @DisplayName("Should throw an exception when no SWIFT code is found for the given country")
    void shouldThrowException_whenSwiftCodeWithCountryISO2NotFound() {
        // Given
        when(swiftCodeRepository.findByCountryISO2("XX")).thenReturn(List.of());

        // When & Then
        CountryISOCodeNotFoundException exception = assertThrows(CountryISOCodeNotFoundException.class,
                () -> swiftCodeService.getSwiftCodeByCountryISO2("XX"));

        assertEquals("SWIFT code with countryISO2 code: XX not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully create a new SWIFT code")
    void shouldCreateSwiftCodeSuccessfully() {
        // Given
        when(swiftCodeRepository.findBySwiftCode(swiftCodeDto.getSwiftCode())).thenReturn(Optional.empty());
        when(swiftCodeMapper.toSwiftCode(swiftCodeDto)).thenReturn(swiftCode);

        // When
        MessageResponse response = swiftCodeService.createSwiftCode(swiftCodeDto);

        // Then
        assertNotNull(response);
        assertEquals("SWIFT code has been added successfully.", response.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception when SWIFT code already exists")
    void shouldThrowException_whenSwiftCodeAlreadyExists() {
        // Given
        when(swiftCodeRepository.findBySwiftCode(swiftCodeDto.getSwiftCode())).thenReturn(Optional.of(swiftCode));

        // When & Then
        SwiftCodeAlreadyExistsException exception = assertThrows(SwiftCodeAlreadyExistsException.class,
                () -> swiftCodeService.createSwiftCode(swiftCodeDto));

        assertEquals("SWIFT code ABCDEFXXX already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully delete an existing SWIFT code")
    void shouldDeleteSwiftCodeSuccessfully() {
        // Given
        when(swiftCodeRepository.findBySwiftCode("ABCDEFXXX")).thenReturn(Optional.of(swiftCode));

        // When
        MessageResponse response = swiftCodeService.deleteSwiftCode("ABCDEFXXX");

        // Then
        assertNotNull(response);
        assertEquals("SWIFT code has been deleted successfully.", response.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception when SWIFT code is not found during deletion")
    void shouldThrowException_whenDeletingSwiftCodeNotFound() {
        // Given
        when(swiftCodeRepository.findBySwiftCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        SwiftCodeNotFoundException exception = assertThrows(SwiftCodeNotFoundException.class,
                () -> swiftCodeService.deleteSwiftCode("INVALID"));

        assertEquals("SWIFT code: INVALID not found", exception.getMessage());
    }
}