package com.co.igg.catastro.bpmn.http;

import java.util.Map;

import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.co.igg.catastro.bpmn.interfaces.IProcessService;

@RestController
@RequestMapping("/engine-rest")
public class BpmnController {
    private final IProcessService processService;

    @Autowired
    public BpmnController(IProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/process-definition/key/{aProcessDefinitionKey}/start")
    public String startProcess(@PathVariable("aProcessDefinitionKey") String processDefinitionKey,
            @RequestBody String body) {
        return processService.createBpmnProcess(processDefinitionKey, body);
    }

    /*
     * @PostMapping("/process-instance/key/{processInstanceId}/change-message-3")
     * public String changeMessageThree(@RequestHeader Map<String, Object>
     * headers, @PathVariable Long processInstanceId,
     * 
     * @RequestBody String body) {
     * return processService.changeMessageRest(processInstanceId, body);
     * }
     */

    @PostMapping("/1")
    public String test() {
        return "Proceso iniciado correctamente";
    }
}