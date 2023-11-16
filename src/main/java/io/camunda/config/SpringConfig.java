package io.camunda.config;

import io.camunda.impl.CmisService;
import io.camunda.interfaces.ICmisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"io.camunda"})
public class SpringConfig {
  @Bean
  public ICmisService cmisService() {
    return new CmisService();
  }
}
