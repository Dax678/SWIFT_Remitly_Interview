package org.example.swift_remitly_interview;

import org.springframework.boot.SpringApplication;

public class TestSwiftRemitlyInterviewApplication {

    public static void main(String[] args) {
        SpringApplication.from(SwiftRemitlyInterviewApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
