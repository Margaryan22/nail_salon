package Frolov_back.NAILS_WEB_APP.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:3000}")
    private String serverPort;

    @Bean
    public OpenAPI nailsSalonOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("NAILS SALON API")
                        .description("""
                            ## 🎯 Полное API для системы управления салоном красоты
                            
                            ### 📋 Основные возможности:
                            - **Аутентификация и регистрация** пользователей
                            - **Управление пользователями** (клиенты, мастера, администраторы)
                            - **Система ролей и прав доступа**
                            - **JWT токены** для безопасного доступа
                            
                            ### 👥 Роли пользователей:
                            - **CLIENT** - клиенты салона
                            - **MASTER** - мастера салона
                            - **ADMIN** - администраторы системы
                            
                            ### 🔐 Аутентификация:
                            1. Зарегистрируйтесь через `/api/v1/auth/register`
                            2. Войдите через `/api/v1/auth/login` для получения JWT токена
                            3. Используйте токен в заголовке `Authorization: Bearer {token}`
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Backend Team - solo")
                                .email("nikitka_frolov_2014@inbox.ru"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Текущий сервер")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Введите JWT токен полученный при логине")));
    }
}