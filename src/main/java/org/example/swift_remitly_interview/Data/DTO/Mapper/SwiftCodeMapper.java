package org.example.swift_remitly_interview.Data.DTO.Mapper;

import org.example.swift_remitly_interview.Data.DTO.CountryISODto;
import org.example.swift_remitly_interview.Data.DTO.SwiftCodeDto;
import org.example.swift_remitly_interview.Data.Entity.SwiftCode;

import java.util.List;

public interface SwiftCodeMapper {
    SwiftCodeDto toSwiftCodeDto(SwiftCode swiftCode);

    CountryISODto toCountryISODto(List<SwiftCode> swiftCodeList);

    SwiftCode toSwiftCode(SwiftCodeDto swiftCodeDto);
}
