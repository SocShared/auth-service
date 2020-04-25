package ml.socshared.template.service.impl;

import ml.socshared.template.client.TestFeignClient;
import ml.socshared.template.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    private TestFeignClient client;

    @Autowired
    public TestServiceImpl(TestFeignClient feignClient) {
        this.client = feignClient;
    }

    @Override
    public String test() {
        return client.test();
    }
}
