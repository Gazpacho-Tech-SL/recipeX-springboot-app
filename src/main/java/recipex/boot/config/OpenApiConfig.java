package recipex.boot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "recipeX API",
        version = "2.0",
        description = "API for managing recipes and user interactions.",
        contact = @Contact(
            name = "Raphael Eze",
            email = "raphaeleze@gmail.com",
            url = "https://www.linkedin.com/in/eze-raphael-echimina-a99177235/"
        )
    )
)
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("recipeX-api")
        .pathsToMatch("/**")
        .build();
  }
}