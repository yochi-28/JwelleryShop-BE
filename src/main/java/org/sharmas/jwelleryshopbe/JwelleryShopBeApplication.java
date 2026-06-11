package org.sharmas.jwelleryshopbe;

import org.sharmas.jwelleryshopbe.models.UserModel;
import org.sharmas.jwelleryshopbe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Date;

@SpringBootApplication
@EnableMongoAuditing
public class JwelleryShopBeApplication {

    public static void main(String[] args) {
        // Load .env file (defaults to searching in current working directory)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Push each entry into System properties, so Spring Boot can resolve ${...}
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        System.out.println("PORT from .env = " + System.getProperty("PORT"));
        System.out.println("DB_URL from .env = " + System.getProperty("DB_URL"));
        SpringApplication.run(JwelleryShopBeApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
//                        .allowedOrigins("http://localhost:5173");
                        .allowedOriginPatterns("*");
            }
        };
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if the admin already exists so we don't create duplicates every time we restart
            if (userRepository.findByUsername("admin").isEmpty()) {

                UserModel defaultAdmin = new UserModel();
                defaultAdmin.setUsername("admin");
                defaultAdmin.setEmail("admin@jewelleryshop.com");

                // CRITICAL: Encode the password before saving!
                defaultAdmin.setPassword(passwordEncoder.encode("admin123"));

                defaultAdmin.setRole(UserModel.Role.ADMIN);
                defaultAdmin.setCreationDate(new Date());

                userRepository.save(defaultAdmin);
                System.out.println("Default Admin User created successfully!");
            } else {
                System.out.println("Admin user already exists. Skipping creation.");
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}