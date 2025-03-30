package org.example.swift_remitly_interview.Data.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "swift_code", schema = "public")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class SwiftCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "countryISO2")
    private String countryISO2;

    @Column(name = "swift_code")
    private String swiftCode;

    @Column(name = "code_type")
    private String codeType;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "address")
    private String address;

    @Column(name = "town_name")
    private String townName;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "is_headquarter")
    private Boolean isHeadquarter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_swift_code_id")
    private SwiftCode parentSwiftCode;

    @OneToMany(mappedBy = "parentSwiftCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SwiftCode> branches;
}
