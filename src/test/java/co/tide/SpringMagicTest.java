package co.tide;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
@AutoConfigureWireMock(port = 0)
public class SpringMagicTest {

    @Autowired
    private A a;
    @Autowired
    private B b;

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    @BeforeEach
    void setup() {
        WireMock.configureFor("localhost", wiremockPort);
    }

    @Test
    void wrongErrorDecoderIsUsed() {

        stubFor(get(urlEqualTo("/a"))
                .willReturn(aResponse().withStatus(400)));
        stubFor(get(urlEqualTo("/b"))
                .willReturn(aResponse().withStatus(400)));

        assertThrows(AException.class, () -> a.a());
        assertThrows(BException.class, () -> b.b());
    }
}