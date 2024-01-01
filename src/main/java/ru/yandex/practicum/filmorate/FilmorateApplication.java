package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication implements CommandLineRunner {

    @Value("${server.port}")
    private String serverPort;

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

    public void run(String... args) {
        System.out.println("Filmorate path: https://localhost:" + serverPort + "/films");
    }
}
