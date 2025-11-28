package com.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Map /uploads/** to both Backend/uploads and project-level ../uploads
        Path backendUploadDir = Paths.get("uploads");
        Path projectUploadDir = Paths.get("..", "uploads");
        String backendUploadPath = backendUploadDir.toFile().getAbsolutePath();
        String projectUploadPath = projectUploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:" + backendUploadPath + "/",
                        "file:" + projectUploadPath + "/"
                )
                .setCachePeriod(0);
    }
}
