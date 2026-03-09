package co.tide;

import feign.Feign;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AFeignConfig {

    @Bean
    ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> new AException();
    }

    @Bean
    public Feign.Builder aBuilder() {
        return Feign.builder().errorDecoder((methodKey, response) -> new AException());
    }

}
