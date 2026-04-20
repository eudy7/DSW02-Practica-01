package com.prcatica01.empleado.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI empleadoOpenApi() {
        final String securityName = "basicAuth";
        return new OpenAPI()
            .info(new Info()
                .title("API de Empleados")
                .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList(securityName))
            .components(new Components().addSecuritySchemes(
                securityName,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")
            ));
    }
}
