package com.mecanix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MecanixApplication {
    public static void main(String[] args) {
        SpringApplication.run(MecanixApplication.class, args);
    }
}
