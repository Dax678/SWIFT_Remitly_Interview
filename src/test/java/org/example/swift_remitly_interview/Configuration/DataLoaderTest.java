package org.example.swift_remitly_interview.Configuration;

import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.example.swift_remitly_interview.Repository.SwiftCodeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class DataLoaderTest {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private DataLoader dataLoader;

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
    }

    @Test
    @DisplayName("Should load and save SWIFT codes from Excel into the database")
    void shouldLoadAndSaveSwiftCodesIntoDatabase() {
        String swift_code = "BREXPLPWXXX";
        String branch_swift_code = "BREXPLPWBIA";

        dataLoader.run();

        List<SwiftCode> allCodes = swiftCodeRepository.findAll();
        assertFalse(allCodes.isEmpty());

        SwiftCode hq = swiftCodeRepository.findBySwiftCode(swift_code).orElseThrow();
        assertTrue(hq.getIsHeadquarter());

        SwiftCode branch = swiftCodeRepository.findBySwiftCode(branch_swift_code).orElseThrow();
        assertFalse(branch.getIsHeadquarter());
        assertNotNull(branch.getParentSwiftCode());
    }

    @Test
    @DisplayName("Should correctly set parentSwiftCode for branches")
    void shouldCorrectlySetParentSwiftCodeForBranches() {
        dataLoader.run();

        List<SwiftCode> branches = swiftCodeRepository.findAll()
                .stream()
                .filter(code -> !code.getIsHeadquarter())
                .toList();

        assertFalse(branches.isEmpty(), "There should be branches in the database");
    }
}