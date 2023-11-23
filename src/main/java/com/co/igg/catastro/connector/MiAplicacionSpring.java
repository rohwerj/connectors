package io.camunda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.connector.runtime.core.outbound.ConnectorJobHandler;
import io.camunda.impl.CmisService;
import io.camunda.interfaces.ICmisService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.connector.Base64Function;

@SpringBootApplication
public class MiAplicacionSpring {
  @Autowired
  private static ICmisService cmisService;
  public static void main(String[] args) {
    //SpringApplication.run(MiAplicacionSpring.class, args);
    
    var zeebeClient = ZeebeClient.newClientBuilder().build();

    zeebeClient.newWorker()
        .jobType("io.camunda:upload-document:1")
        .handler(new ConnectorJobHandler(new Base64Function(cmisService), null))
        .name("UploadToAlfresco")
        .fetchVariables("files", "filesNames", "idProceso")
        .open();
  }
}
