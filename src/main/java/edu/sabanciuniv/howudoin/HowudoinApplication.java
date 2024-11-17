package edu.sabanciuniv.howudoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class HowudoinApplication {

    public static void main(String[] args) {
        SpringApplication.run(HowudoinApplication.class, args);
        System.out.println("Howudoin Application Started");
    }
}