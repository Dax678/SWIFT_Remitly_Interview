package org.example.swift_remitly_interview.Configuration;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.example.swift_remitly_interview.Repository.SwiftCodeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {
    private final SwiftCodeRepository swiftCodeRepository;

    private static final String FILE_PATH = "Interns_2025_SWIFT_CODES.xlsx";

    public DataLoader(SwiftCodeRepository swiftCodeRepository) {
        this.swiftCodeRepository = swiftCodeRepository;
    }

    @Override
    public void run(String... args) {
        try(InputStream file = getClass().getClassLoader().getResourceAsStream(FILE_PATH)) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            List<SwiftCode> headquarters = new ArrayList<>();
            List<SwiftCode> branches = new ArrayList<>();
            Map<String, SwiftCode> swiftCodeMap = new HashMap<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                SwiftCode swiftCode = parseRowToSwiftCode(row);

                if (swiftCode.getSwiftCode().endsWith("XXX")) {
                    swiftCode.setIsHeadquarter(true);
                    headquarters.add(swiftCode);
                } else {
                    swiftCode.setIsHeadquarter(false);
                    branches.add(swiftCode);
                }
            }

            // Save headquarters to db
            List<SwiftCode> savedHeadquarters = swiftCodeRepository.saveAll(headquarters);
            savedHeadquarters.forEach(hq -> swiftCodeMap.put(hq.getSwiftCode(), hq));

            for (SwiftCode branch : branches) {
                String parentSwiftCode = branch.getSwiftCode().substring(0, 8) + "XXX";
                SwiftCode parent = swiftCodeMap.get(parentSwiftCode);
                if (parent != null) {
                    branch.setParentSwiftCode(parent);
                }
            }

            // Save branches to db
            swiftCodeRepository.saveAll(branches);
            workbook.close();

        } catch (Exception e) {
            System.err.println("Unexpected error while reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private SwiftCode parseRowToSwiftCode(Row row) {
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCountryISO2(row.getCell(0).getStringCellValue().toUpperCase());
        swiftCode.setSwiftCode(row.getCell(1).getStringCellValue());
        swiftCode.setCodeType(row.getCell(2).getStringCellValue());
        swiftCode.setBankName(row.getCell(3).getStringCellValue());

        String address = row.getCell(4).getStringCellValue();
        if(address.isBlank()) {
            swiftCode.setAddress(null);
        } else {
            swiftCode.setAddress(row.getCell(4).getStringCellValue());
        }

        swiftCode.setTownName(row.getCell(5).getStringCellValue());
        swiftCode.setCountryName(row.getCell(6).getStringCellValue().toUpperCase());
        swiftCode.setTimezone(row.getCell(7).getStringCellValue());
        return swiftCode;
    }
}
