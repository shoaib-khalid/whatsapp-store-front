package com.kalsym.whatsapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
//@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
public class WhatsappServiceApplication  implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger("application");

    static {
        System.setProperty("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        /**
         * To escape SQL reserved keywords
         */
        System.setProperty("hibernate.globally_quoted_identifiers", "true");
    }

    public static String VERSION;

    @Autowired
    private Environment env;

    public static void main(String... args) {
        logger.info("Starting whatsapp-wrapper-service...");
        SpringApplication.run(WhatsappServiceApplication.class, args);
    }


    @Value("${build.version:not-known}")
    String version;

	@Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }



	// public static void main(String[] args) {
	// 	SpringApplication.run(WhatsappServiceApplication.class, args);
	// }

    @Override
    public void run(String... args) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
