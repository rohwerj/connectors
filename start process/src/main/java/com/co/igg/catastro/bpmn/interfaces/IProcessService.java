package com.co.igg.catastro.bpmn.interfaces;

import org.springframework.boot.CommandLineRunner;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import com.co.igg.catastro.bpmn.services.ProcessService.*;
import com.co.igg.catastro.bpmn.services.dto.MyConnectorRequest;

public interface IProcessService extends CommandLineRunner {

    public String createBpmnProcess(String processDefinitionKey, String body);

    // public String changeMessageRest(Long processInstanceId, String body);

    // public void completarTrabajo(JobClient jobClient, ActivatedJob job,
    // MyConnectorRequest variables);

}
