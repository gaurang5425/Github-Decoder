package com.example.github.github_Decoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class GithubDecoderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubDecoderApplication.class, args);
	
	}
	 @Bean
		

	    public WebMvcConfigurer corsConfigurer() {
		 return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	            	//hello
	            	registry.addMapping("/**")
                 .allowedOrigins("http://localhost:5173")
                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                 .allowedHeaders("*") // Allow all headers
                 .allowCredentials(true)
                 .maxAge(3600); ;
               
	            }
	        };

}}

//mvn clean install
//mvn spring-boot:run