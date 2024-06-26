package com.maticz.ER.AC.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maticz.ER.AC.service.impl.ACServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/AC")
public class ERController {

    @Autowired
    ACServiceImpl acService;
    @GetMapping("sendToAC")
    @Scheduled(cron = "0 20 4 * * *")
    ResponseEntity<String> saveToAC() throws JsonProcessingException {

            acService.runQueryAndUpdate();

        return ResponseEntity.ok("ok");
    }

}
