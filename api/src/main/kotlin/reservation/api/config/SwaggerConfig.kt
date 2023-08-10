package reservation.api.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(info = Info(title = "Reservation API", description = "lotte healthcare reservation api 명세", version = "v1"))
@Configuration
class SwaggerConfig {
    @Bean
    fun chatOpenApi(): GroupedOpenApi {
        val paths = arrayOf("/api/**")
        return GroupedOpenApi.builder()
                .group("lotte healthcare reservation API v1")
                .pathsToMatch(*paths)
                .build()
    }
}