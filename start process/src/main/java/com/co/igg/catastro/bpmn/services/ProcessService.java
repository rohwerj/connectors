package com.co.igg.catastro.bpmn.services;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.co.igg.catastro.bpmn.interfaces.IProcessService;
import com.co.igg.catastro.bpmn.services.dto.MyConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;

import org.springframework.http.HttpStatus;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.connect.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;

import java.time.Duration;

import io.camunda.connector.api.annotation.OutboundConnector;

import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.runtime.util.outbound.ConnectorJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProcessService implements IProcessService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private MyConnectorRequest variables = new MyConnectorRequest();

    @Autowired
    private ZeebeClient client;

    @Override
    @Transactional
    public String createBpmnProcess(String processDefinitionKey, String body) {
        System.out.println("Entrando en createBpmnProcess");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MyConnectorRequest variables = objectMapper.readValue(body, MyConnectorRequest.class);
            final ProcessInstanceEvent processInstanceEvent = client
                    .newCreateInstanceCommand()
                    .bpmnProcessId(processDefinitionKey)
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();
            Long idProceso = processInstanceEvent.getProcessInstanceKey();
            System.out.println("Deployment successful. Workflow deployed with key:"+idProceso);
            System.out.println("Saliendo de createBpmnProcess");

            // client.close();
            return String.valueOf(idProceso);
        } catch (Exception e) {
            logger.error("Error: {}", e.toString());
            System.out.println("Saliendo de createBpmnProcess");
            // client.close();
            return "error";
        }
    }


    @Override
    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub

    }
}