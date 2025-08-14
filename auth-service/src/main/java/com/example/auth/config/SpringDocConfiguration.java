package com.example.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration(proxyBeanMethods = false)
public class SpringDocConfiguration {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(apiInfo())
                .schemaRequirement("bearer-token", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-token"));
    }

    private Info apiInfo() {
        Contact contact = new Contact()
                .name("Users");
        return new Info().contact(contact);
    }
}
