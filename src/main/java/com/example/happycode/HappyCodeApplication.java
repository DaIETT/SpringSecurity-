package com.example.happycode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled=true,prePostEnabled = true)

public class HappyCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappyCodeApplication.class, args);
    }

}
