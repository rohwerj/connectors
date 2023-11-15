package io.camunda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.camunda.impl.CmisService;
import io.camunda.interfaces.ICmisService;

@Configuration
@ComponentScan({"io.camunda"})
public class SpringConfig {
	@Bean
    public ICmisService cmisService() {
        return new CmisService();
    }
}
