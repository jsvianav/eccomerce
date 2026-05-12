package co.edu.usbcali.ecommerceusb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Configura los metadatos globales de la documentación OpenAPI 3 (Swagger).
     * Al levantar el proyecto, la UI estará disponible en: http://localhost:8080/swagger-ui/index.html
     * El JSON de la especificación estará disponible en: http://localhost:8080/v3/api-docs
     */
    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce USB API")
                        .description("Documentación de los endpoints REST del microservicio de ecommerce - Universidad San Buenaventura Cali")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("USB Cali - Proyecto Ecommerce")
                                .email("ecommerce@usbcali.edu.co"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
