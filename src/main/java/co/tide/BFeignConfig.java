package co.tide;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class BFeignConfig {

    @Bean
    public Feign.Builder bBuilder() {

        return Feign.builder().errorDecoder((methodKey, response) -> new BException());
    }
}
