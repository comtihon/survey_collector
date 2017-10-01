package com.surveyor.collector.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Value("${spring.data.mongodb.collection}")
    private String collection;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public String getCollection() {
        return collection;
    }
}
