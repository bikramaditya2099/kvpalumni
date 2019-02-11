package com.bikram;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import com.bikram.utility.ApplicationContextHolder;
import com.bikram.utility.CreateDefaults;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class MainController {


	public static void main(String[] args) {
		SpringApplication.run(MainController.class, args);
		CreateDefaults createDefaultRoles=ApplicationContextHolder.getContext().getBean(CreateDefaults.class);
		createDefaultRoles.CreateRoles();
		createDefaultRoles.CreateAdminUser();
	}
	
}
