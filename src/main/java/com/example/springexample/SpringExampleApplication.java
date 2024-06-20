package com.example.springexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class SpringExampleApplication {
    final static Logger LOGGER = LoggerFactory.getLogger(SpringExampleApplication.class);
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringExampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return args -> {
            System.out.println("Test logging with sout.");
            LOGGER.info("log info");
            LOGGER.trace("log trace");
        };
    }
}
