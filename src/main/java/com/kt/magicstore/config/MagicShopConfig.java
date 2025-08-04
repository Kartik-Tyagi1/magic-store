package com.kt.magicstore.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MagicShopConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
