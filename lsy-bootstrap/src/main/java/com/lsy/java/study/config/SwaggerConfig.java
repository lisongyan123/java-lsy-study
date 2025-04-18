package com.lsy.java.study.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfig implements CommandLineRunner {

    @Value("${server.port:8081}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public Docket api() {
        return new Docket(springfox.documentation.spi.DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    public void run(String... args) throws Exception {
        String swaggerUrl = "http://localhost:" + serverPort + contextPath + "/swagger-ui/index.html";
        System.out.println("Swagger UI is available at: " + swaggerUrl);
    }
}
