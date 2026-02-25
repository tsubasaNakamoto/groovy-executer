package com.example.groovyexecuter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.groovyexecuter")
public class GroovyExecuterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroovyExecuterApplication.class, args);
    }
}
