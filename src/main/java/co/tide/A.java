package co.tide;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "a-client",
        url = "http://localhost:${wiremock.server.port}",
        configuration = {AFeignConfig.class}
)
public interface A {

    @GetMapping("a")
    String a();
}
