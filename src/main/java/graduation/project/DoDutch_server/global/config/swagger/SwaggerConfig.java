package graduation.project.DoDutch_server.global.config.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DoDutch API")
                        .description("여행 기록 저장 및 정산 서비스 API 문서입니다.")
                        .version("v1.0.0")
                );
    }
}
