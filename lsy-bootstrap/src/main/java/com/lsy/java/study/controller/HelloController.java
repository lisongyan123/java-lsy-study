package com.lsy.java.study.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Operation(summary = "Get a greeting message", description = "Returns a simple greeting message")
    @GetMapping("/hello")
    public String sayHello(@Parameter(description = "Name of the person to greet") @RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info(String.format("Hello, %s!", name));
        return String.format("Hello, %s!", name);
    }
}