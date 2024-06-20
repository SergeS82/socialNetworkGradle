package com.example.springexample.controllers;

import com.example.springexample.dto.AuthorDto;
import com.example.springexample.dto.SubscriptionDto;
import com.example.springexample.entity.Author;
import com.example.springexample.entity.Subscription;
import com.example.springexample.repository.AuthorRepository;
import com.example.springexample.repository.SubscriptionRepository;
import org.springframework.context.annotation.Configuration;
import com.example.springexample.test_classes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
public class IntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);


    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final GenericContainer<?>
            pgContainer = new GenericContainer<>(DockerImageName.parse("sergs82/socialnetwork-db:latest"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "socialnetwork")
            .withEnv("POSTGRES_USER", "admin")
            .withEnv("POSTGRES_PASSWORD", "root");
    @BeforeAll
    void startContainer() {
        pgContainer.start();
        String host = pgContainer.getHost();
        Integer port = pgContainer.getMappedPort(5432);
        System.setProperty("spring.datasource.url", "jdbc:postgresql://" + host + ":" + port + "/socialnetwork");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    @DisplayName("load authors")
    void createSomeAuthors() throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TestEntity<Author, AuthorDto> testEntity = new TestEntity<>("/test_data/author_data.json", AuthorDto.class, authorRepository, webApplicationContext);
        testEntity.postAll("/author");
    }
    @Test
    @DisplayName("load subscriptions")
    void createSomeSubscriptions() throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        TestEntity<Subscription, SubscriptionDto> testEntity = new TestEntity<>("/test_data/subscription_data.json", SubscriptionDto.class, subscriptionRepository, webApplicationContext);
        testEntity.postAll("/subscription");
    }

    @Configuration
    static class TestConfig {

    }
}
