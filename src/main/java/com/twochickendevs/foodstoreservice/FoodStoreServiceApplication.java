package com.twochickendevs.foodstoreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.twochickendevs"})
@EnableTransactionManagement
public class FoodStoreServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(FoodStoreServiceApplication.class, args);
	}
}
