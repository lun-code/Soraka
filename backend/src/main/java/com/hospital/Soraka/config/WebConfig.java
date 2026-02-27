package com.hospital.Soraka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea /uploads/** a la carpeta física de tu backend
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/"); // ruta relativa a donde corre la app en docker
    }
}