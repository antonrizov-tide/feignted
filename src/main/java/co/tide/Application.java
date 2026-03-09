package co.tide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "co.tide")
@EnableFeignClients(basePackages = "co.tide")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
