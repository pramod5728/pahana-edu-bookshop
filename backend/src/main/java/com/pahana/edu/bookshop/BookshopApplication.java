package com.pahana.edu.bookshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Pahana Edu Bookshop Management System
 * 
 * This Spring Boot application provides REST APIs for managing customers,
 * inventory, billing, and user authentication for the bookshop.
 * 
 * @author Pahana Edu Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class BookshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookshopApplication.class, args);
    }
}