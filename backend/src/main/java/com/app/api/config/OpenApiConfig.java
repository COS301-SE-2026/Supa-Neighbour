package main.java.com.app.api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Social App API")
                .description("API documentation for the Social App backend")
                .version("1.0.0"));
    }
}