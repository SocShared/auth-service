package ml.socshared.template.controller.v1;

import ml.socshared.template.exception.impl.HttpBadRequestException;
import ml.socshared.template.service.TestService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import ml.socshared.template.api.v1.rest.HelloApi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1")
public class HelloController implements HelloApi {

    private TestService service;

    public HelloController(TestService service) {
        this.service = service;
    }

    @Override
    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, String> printHelloWorld() {

        return new HashMap<>() {
            {
                put("text", "Hello, World");
            }
        };
    }

    @GetMapping(value = "/feign", produces = MediaType.APPLICATION_JSON_VALUE)
    public String testFeign() {
        return service.test();
    }

}
