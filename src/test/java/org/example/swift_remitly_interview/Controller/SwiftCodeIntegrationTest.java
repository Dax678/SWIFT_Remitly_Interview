package org.example.swift_remitly_interview.Controller;

import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.example.swift_remitly_interview.Repository.SwiftCodeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public class SwiftCodeIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    private List<SwiftCode> swiftCodes;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @BeforeAll
    static void setUpDatabase() {
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }

    @BeforeEach
    void setUp() {
        swiftCodeRepository.deleteAll();

        swiftCodes = new ArrayList<>();
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCountryISO2("PL");
        swiftCode.setSwiftCode("ABCDEFGHXXX");
        swiftCode.setCodeType("BIC11");
        swiftCode.setBankName("Bank Name Test 1");
        swiftCode.setAddress("Address Test");
        swiftCode.setTownName("WARSZAWA");
        swiftCode.setCountryName("POLAND");
        swiftCode.setTimezone("Europe/Warsaw");
        swiftCode.setIsHeadquarter(true);
        swiftCodes.add(swiftCode);

        swiftCode = new SwiftCode();
        swiftCode.setCountryISO2("PL");
        swiftCode.setSwiftCode("ABCDEFGH1");
        swiftCode.setCodeType("BIC11");
        swiftCode.setBankName("Bank Name Test 2");
        swiftCode.setAddress("Address Test");
        swiftCode.setTownName("WARSZAWA");
        swiftCode.setCountryName("POLAND");
        swiftCode.setTimezone("Europe/Warsaw");
        swiftCode.setIsHeadquarter(false);
        swiftCode.setParentSwiftCode(swiftCodes.get(0));
        swiftCodes.add(swiftCode);

        swiftCodeRepository.saveAll(swiftCodes);
    }

    @Test
    @DisplayName("GET /v1/swift-codes/ABCDEFGHXXX should return SwiftCodeDto object")
    void shouldReturnSwiftCodeDto_whenSwiftCodeExists() throws Exception {
        String swift_code = "ABCDEFGHXXX";
        mockMvc.perform(get("/v1/swift-codes/" + swift_code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value(swift_code))
                .andExpect(jsonPath("$.bankName").value(swiftCodes.get(0).getBankName()))
                .andExpect(jsonPath("$.countryISO2").value(swiftCodes.get(0).getCountryISO2()))
                .andExpect(jsonPath("$.branches[0].swiftCode").value(swiftCodes.get(1).getSwiftCode()));
    }

    @Test
    @DisplayName("GET /v1/swift-codes/ABCDEFGH1 should return SwiftCodeDto without 'branches' field when SWIFT code belongs to a branch")
    void shouldReturnSwiftCodeDtoWithoutBranches_whenSwiftCodeBelongsToBranch() throws Exception {
        String swift_code = "ABCDEFGH1";
        mockMvc.perform(get("/v1/swift-codes/" + swift_code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value(swift_code))
                .andExpect(jsonPath("$.bankName").value(swiftCodes.get(1).getBankName()))
                .andExpect(jsonPath("$.countryISO2").value(swiftCodes.get(1).getCountryISO2()))
                .andExpect(jsonPath("$.branches").doesNotExist());
    }

    @Test
    @DisplayName("GET /v1/swift-codes/INVALID should return 404 when SWIFT code is not found")
    void shouldReturn404_whenSwiftCodeNotFound() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code: INVALID not found"));
    }

    @Test
    @DisplayName("GET /v1/swift-codes/country/PL should return CountryISODto when the SWIFT code exists for a given country")
    void shouldReturnCountryISODto_whenSwiftCodeExists() throws Exception {
        String countryISO2code = "PL";
        mockMvc.perform(get("/v1/swift-codes/country/" + countryISO2code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value(countryISO2code))
                .andExpect(jsonPath("$.countryName").value("POLAND"))
                .andExpect(jsonPath("$.swiftCodes[0].countryISO2").value(countryISO2code))
                .andExpect(jsonPath("$.swiftCodes[0].swiftCode").value(swiftCodes.get(0).getSwiftCode()))
                .andExpect(jsonPath("$.swiftCodes[1].countryISO2").value(countryISO2code))
                .andExpect(jsonPath("$.swiftCodes[1].swiftCode").value(swiftCodes.get(1).getSwiftCode()));
    }

    @Test
    @DisplayName("GET /v1/swift-codes/country/XX should throw an exception when no SWIFT code is found for the given country")
    void shouldThrowException_whenSwiftCodeWithCountryISO2NotFound() throws Exception {
        String countryISO2code = "XX";
        mockMvc.perform(get("/v1/swift-codes/country/" + countryISO2code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code with countryISO2 code: XX not found"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should create a new SWIFT code")
    void shouldCreateSwiftCodeSuccessfully() throws Exception {
        Map<String, Object> newSwiftCodeDto = new HashMap<>();
        newSwiftCodeDto.put("address", "Address Test");
        newSwiftCodeDto.put("bankName", "Bank Name Test 3");
        newSwiftCodeDto.put("countryISO2", "PL");
        newSwiftCodeDto.put("countryName", "POLAND");
        newSwiftCodeDto.put("isHeadquarter", true);
        newSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(newSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("SWIFT code has been added successfully."));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return conflict when SWIFT code already exists")
    void shouldReturnConflict_whenSwiftCodeAlreadyExists() throws Exception {
        Map<String, Object> newSwiftCodeDto = new HashMap<>();
        newSwiftCodeDto.put("address", "Address Test");
        newSwiftCodeDto.put("bankName", "Bank Name Test 3");
        newSwiftCodeDto.put("countryISO2", "PL");
        newSwiftCodeDto.put("countryName", "POLAND");
        newSwiftCodeDto.put("isHeadquarter", true);
        newSwiftCodeDto.put("swiftCode", "ABCDEFGHXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(newSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("SWIFT code ABCDEFGHXXX already exists"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when swiftCode is empty")
    void shouldReturnBadRequest_whenSwiftCodeIsEmpty() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "PL");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("swiftCode: SWIFT code cannot be empty"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when swiftCode is too short")
    void shouldReturnBadRequest_whenSwiftCodeIsTooShort() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "PL");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "ABC");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("swiftCode: SWIFT code must be either 8 or 11 characters long"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when address is empty")
    void shouldReturnBadRequest_whenAddressIsEmpty() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "PL");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("address: Address cannot be empty"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when bankName is empty")
    void shouldReturnBadRequest_whenBankNameIsEmpty() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "");
        invalidSwiftCodeDto.put("countryISO2", "PL");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("bankName: Bank name cannot be empty"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when countryISO2 is empty")
    void shouldReturnBadRequest_whenCountryISO2IsEmpty() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("countryISO2: Country ISO2 code cannot be empty"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when countryISO2 is too long")
    void shouldReturnBadRequest_whenCountryISO2IsTooLong() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "XXX");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("countryISO2: Country ISO2 code must be exactly 2 characters long"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when countryName is empty")
    void shouldReturnBadRequest_whenCountryNameIsEmpty() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "PL");
        invalidSwiftCodeDto.put("countryName", "");
        invalidSwiftCodeDto.put("isHeadquarter", true);
        invalidSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("countryName: Country name cannot be empty"));
    }

    @Test
    @DisplayName("POST /v1/swift-codes should return bad request when isHeadquarter is null")
    void shouldReturnBadRequest_whenIsHeadquarterIsNull() throws Exception {
        Map<String, Object> invalidSwiftCodeDto = new HashMap<>();
        invalidSwiftCodeDto.put("address", "Address Test");
        invalidSwiftCodeDto.put("bankName", "Bank Name Test 3");
        invalidSwiftCodeDto.put("countryISO2", "PL");
        invalidSwiftCodeDto.put("countryName", "POLAND");
        invalidSwiftCodeDto.put("isHeadquarter", null);
        invalidSwiftCodeDto.put("swiftCode", "HGFEDCBAXXX");

        String jsonBody = new ObjectMapper().writeValueAsString(invalidSwiftCodeDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("isHeadquarter: Headquarter status cannot be null"));
    }

    @Test
    @DisplayName("DELETE /v1/swift-codes/ABCDEFGHXXX should delete a SWIFT code successfully")
    void shouldDeleteSwiftCodeSuccessfully() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/ABCDEFGHXXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code has been deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /v1/swift-codes/INVALID should return 404 when deleting a non-existent SWIFT code")
    void shouldReturn404_whenDeletingNonExistentSwiftCode() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code: INVALID not found"));

    }
}
