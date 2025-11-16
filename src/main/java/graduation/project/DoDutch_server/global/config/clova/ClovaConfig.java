package graduation.project.DoDutch_server.global.config.clova;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class ClovaConfig {

    @Bean
    public RestTemplate clovaRestTemplate() {
        return new RestTemplate();
    }
}
