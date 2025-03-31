package org.example.swift_remitly_interview.Service;

import lombok.AllArgsConstructor;
import org.example.swift_remitly_interview.Configuration.Exception.CountryISOCodeNotFoundException;
import org.example.swift_remitly_interview.Configuration.Exception.SwiftCodeAlreadyExistsException;
import org.example.swift_remitly_interview.Configuration.Exception.SwiftCodeDatabaseException;
import org.example.swift_remitly_interview.Configuration.Exception.SwiftCodeNotFoundException;
import org.example.swift_remitly_interview.Data.DTO.CountryISODto;
import org.example.swift_remitly_interview.Data.DTO.Mapper.SwiftCodeMapperImpl;
import org.example.swift_remitly_interview.Data.DTO.MessageResponse;
import org.example.swift_remitly_interview.Data.DTO.SwiftCodeDto;
import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.example.swift_remitly_interview.Repository.SwiftCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SwiftCodeService {
    private static final Logger logger = LoggerFactory.getLogger(SwiftCodeService.class);
    private final SwiftCodeRepository swiftCodeRepository;
    private final SwiftCodeMapperImpl swiftCodeMapper;

    public SwiftCodeDto getSwiftCode(String swiftCode) {
        Optional<SwiftCode> optionalSwiftCode = swiftCodeRepository.findBySwiftCode(swiftCode);

        if(optionalSwiftCode.isEmpty()) {
            logger.warn("SWIFT code {} not found", swiftCode);
            throw new SwiftCodeNotFoundException("SWIFT code: {0} not found", swiftCode);
        }

        return swiftCodeMapper.toSwiftCodeDto(optionalSwiftCode.get());
    }

    public CountryISODto getSwiftCodeByCountryISO2(String countryISO2code) {
        List<SwiftCode> optionalSwiftCode = swiftCodeRepository.findByCountryISO2(countryISO2code);

        if(optionalSwiftCode.isEmpty()) {
            logger.warn("SWIFT code with countryISO2 code: {} not found", countryISO2code);
            throw new CountryISOCodeNotFoundException("SWIFT code with countryISO2 code: {0} not found", countryISO2code);
        }

        return swiftCodeMapper.toCountryISODto(optionalSwiftCode);
    }

    @Transactional
    public MessageResponse createSwiftCode(SwiftCodeDto swiftCodeDto) {
        if(swiftCodeRepository.findBySwiftCode(swiftCodeDto.getSwiftCode()).isPresent()) {
            logger.warn("SWIFT code {} already exists", swiftCodeDto.getSwiftCode());
            throw new SwiftCodeAlreadyExistsException("SWIFT code {0} already exists", swiftCodeDto.getSwiftCode());
        }

        try {
            SwiftCode swiftCode = swiftCodeMapper.toSwiftCode(swiftCodeDto);

            if(!swiftCodeDto.getSwiftCode().endsWith("XXX")) {
                String ParentSwiftCode = swiftCodeDto.getSwiftCode().substring(0, 8) + "XXX";
                Optional<SwiftCode> optionalSwiftCode = swiftCodeRepository.findBySwiftCode(ParentSwiftCode);

                if(optionalSwiftCode.isEmpty()) {
                    logger.warn("Parent SWIFT code {} not found", ParentSwiftCode);
                    throw new SwiftCodeNotFoundException("Parent SWIFT code: {0} not found", ParentSwiftCode);
                }

                swiftCode.setParentSwiftCode(optionalSwiftCode.get());
            }

            swiftCodeRepository.save(swiftCode);
            return new MessageResponse("SWIFT code has been added successfully.");
        } catch (DataAccessException ex) {
            logger.error("Database error while saving SWIFT code {}: {}", swiftCodeDto.getSwiftCode(), ex.getMessage());
            throw new SwiftCodeDatabaseException("Failed to save SWIFT code due to database error.", ex);
        }
    }

    @Transactional
    public MessageResponse deleteSwiftCode(String swiftCode) {
        Optional<SwiftCode> optionalSwiftCode = swiftCodeRepository.findBySwiftCode(swiftCode);

        if(optionalSwiftCode.isEmpty()) {
            logger.warn("SWIFT code {} not found", swiftCode);
            throw new SwiftCodeNotFoundException("SWIFT code: {0} not found", swiftCode);
        }

        try {
            swiftCodeRepository.delete(optionalSwiftCode.get());
            return new MessageResponse("SWIFT code has been deleted successfully.");
        } catch (DataAccessException ex) {
            logger.error("Database error while deleting SWIFT code {}: {}", swiftCode, ex.getMessage());
            throw new SwiftCodeDatabaseException("Failed to delete SWIFT code due to database error.", ex);
        }
    }
}
