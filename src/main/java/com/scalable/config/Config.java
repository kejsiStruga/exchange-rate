package com.scalable.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import java.util.function.Predicate;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.scalable"})
public class Config {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.scalable.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfo(
                "Scalable Exchange Rate API",
                "API provides basic functionalities for getting exchange rate info",
                " 0.1",
                "Terms of service",
                new Contact("Kejsi Struga", "https://github.com/kejsiStruga", "kejsi.struga@tum.de"),
                "License of API", "API license URL", Collections.emptyList());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}