package com.api.reservavuelos;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ReservaVuelosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservaVuelosApplication.class, args);
    }
}
