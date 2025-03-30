package org.example.swift_remitly_interview.Repository;

import org.example.swift_remitly_interview.Data.Entity.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Long> {

    Optional<SwiftCode> findBySwiftCode(String swiftCode);

    List<SwiftCode> findByCountryISO2(String countryISO2Code);
}
