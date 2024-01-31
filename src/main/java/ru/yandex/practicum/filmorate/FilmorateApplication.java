package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FilmorateApplication implements CommandLineRunner {
    @Value("${custom.url}")
    private String customUrl;


    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Filmorate started at url: {}", customUrl);
    }
}
