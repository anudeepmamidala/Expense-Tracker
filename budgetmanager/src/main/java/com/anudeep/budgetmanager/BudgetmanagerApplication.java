package com.anudeep.budgetmanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.anudeep.budgetmanager.service.EmailService;

@EnableScheduling
@SpringBootApplication
public class BudgetmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetmanagerApplication.class, args);
	}

	@Bean
public CommandLineRunner testEmail(EmailService emailService) {
    return args -> {
        emailService.sendEmail("saianudeepmamidala@gmail.com", "Test Email", "This is a test email!");
        System.out.println("Email sent!");
    };
}

}

