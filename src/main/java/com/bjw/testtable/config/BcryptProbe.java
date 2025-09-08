package com.bjw.testtable.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BcryptProbe implements CommandLineRunner {
    private final BCryptPasswordEncoder enc;
    @Override public void run(String... args) {
        String raw = "123";
        String encoded = "$2a$10$JfsHLJDV49eNIf2wRxyI5evANBM6fpmu2t3HDmzspXipNisXULmOq";
        System.out.println("matches? " + enc.matches(raw, encoded)); // true 나와야 정상
    }
}
