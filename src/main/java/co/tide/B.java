package co.tide;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "b-client",
        url = "http://localhost:${wiremock.server.port}",
        configuration = {BFeignConfig.class}
)
public interface B {

    @GetMapping("b")
    String b();
}
