package Frolov_back.NAILS_WEB_APP.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI nailsSalonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NAILS SALON API")
                        .description("API для системы управления салоном красоты")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FrolovNM - Backend Team (solo)")
                                .email("nikitka_frolov_2014@inbox.ru")));
    }
}