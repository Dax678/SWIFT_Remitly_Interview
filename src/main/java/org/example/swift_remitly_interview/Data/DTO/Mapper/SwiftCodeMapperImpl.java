package org.example.swift_remitly_interview.Data.DTO.Mapper;

import org.example.swift_remitly_interview.Data.DTO.BranchSwiftCodeDto;
import org.example.swift_remitly_interview.Data.DTO.CountryISODto;
import org.example.swift_remitly_interview.Data.DTO.SwiftCodeDto;
import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SwiftCodeMapperImpl implements SwiftCodeMapper {

    @Override
    public SwiftCodeDto toSwiftCodeDto(SwiftCode swiftCode) {
        if ( swiftCode == null ) return null;

        SwiftCodeDto swiftCodeDto = new SwiftCodeDto();

        swiftCodeDto.setAddress( swiftCode.getAddress() );
        swiftCodeDto.setBankName( swiftCode.getBankName() );
        swiftCodeDto.setCountryISO2( swiftCode.getCountryISO2() );
        swiftCodeDto.setCountryName( swiftCode.getCountryName() );
        swiftCodeDto.setIsHeadquarter( swiftCode.getIsHeadquarter() );
        swiftCodeDto.setSwiftCode( swiftCode.getSwiftCode() );

        if ( !swiftCode.getSwiftCode().endsWith("XXX") && (swiftCode.getBranches() == null || swiftCode.getBranches().isEmpty())) {
            swiftCodeDto.setBranches( null );
        } else {
            List<BranchSwiftCodeDto> branchSwiftCodeDtoList = swiftCode.getBranches().stream()
                    .map(branch -> {
                        BranchSwiftCodeDto branchSwiftCodeDto = new BranchSwiftCodeDto();
                        branchSwiftCodeDto.setAddress( branch.getAddress() );
                        branchSwiftCodeDto.setBankName( branch.getBankName() );
                        branchSwiftCodeDto.setCountryISO2( branch.getCountryISO2() );
                        branchSwiftCodeDto.setIsHeadquarter( branch.getIsHeadquarter() );
                        branchSwiftCodeDto.setSwiftCode( branch.getSwiftCode() );
                        return branchSwiftCodeDto;
                    }).collect(Collectors.toList());
            swiftCodeDto.setBranches(branchSwiftCodeDtoList);
        }

        return swiftCodeDto;
    }

    @Override
    public CountryISODto toCountryISODto(List<SwiftCode> swiftCodeList) {
        if ( swiftCodeList == null || swiftCodeList.isEmpty() ) return null;

        CountryISODto countryISODto = new CountryISODto();

        countryISODto.setCountryISO2(swiftCodeList.getFirst().getCountryISO2());
        countryISODto.setCountryName(swiftCodeList.getFirst().getCountryName());

        List<BranchSwiftCodeDto> branchSwiftCodeDtos = swiftCodeList.stream()
                .map(swiftCode -> {
                    BranchSwiftCodeDto branchSwiftCodeDto = new BranchSwiftCodeDto();
                    branchSwiftCodeDto.setAddress(swiftCode.getAddress());
                    branchSwiftCodeDto.setBankName(swiftCode.getBankName());
                    branchSwiftCodeDto.setCountryISO2(swiftCode.getCountryISO2());
                    branchSwiftCodeDto.setIsHeadquarter(swiftCode.getIsHeadquarter());
                    branchSwiftCodeDto.setSwiftCode(swiftCode.getSwiftCode());
                    return branchSwiftCodeDto;
                }).collect(Collectors.toList());

        countryISODto.setSwiftCodes(branchSwiftCodeDtos);

        return countryISODto;
    }

    @Override
    public SwiftCode toSwiftCode(SwiftCodeDto swiftCodeDto) {
        if ( swiftCodeDto == null ) return null;
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCountryISO2( swiftCodeDto.getCountryISO2() );
        swiftCode.setSwiftCode( swiftCodeDto.getSwiftCode() );
        swiftCode.setCodeType("BIC11");
        swiftCode.setBankName( swiftCodeDto.getBankName() );
        swiftCode.setAddress( swiftCodeDto.getAddress() );
        swiftCode.setTownName( null );
        swiftCode.setCountryName( swiftCodeDto.getCountryName() );
        swiftCode.setTimezone( null );
        swiftCode.setIsHeadquarter( swiftCodeDto.getIsHeadquarter() );
        return swiftCode;
    }
}