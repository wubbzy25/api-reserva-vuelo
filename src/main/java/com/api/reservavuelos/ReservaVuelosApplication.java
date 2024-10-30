package com.api.reservavuelos;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
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
