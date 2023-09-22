package com.example.nearbymarketplace;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NearByMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NearByMarketplaceApplication.class, args);
    }

    @Bean
    public JtsModule jtsModule(){
        //Serializer for geometries
        return new JtsModule();
    }
}
