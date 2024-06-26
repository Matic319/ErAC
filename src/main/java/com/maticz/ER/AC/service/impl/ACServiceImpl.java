package com.maticz.ER.AC.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maticz.ER.AC.repository.ERRepository;
import com.maticz.ER.AC.service.ACService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ACServiceImpl implements ACService {

    @Autowired
    ERRepository erRepository;

    @Value("${ac.api.token}")
    private String acToken;

    Logger logger = LoggerFactory.getLogger(ACServiceImpl.class);

    @Override
    public void updateACValues(String email, String firstSuggestion, String secondSuggestion, String firstSuggestionText, String secondSuggestionText, String firstSuggestionDifficulty,
                               String secondSuggestionDifficulty, String erRetentionText, String locationFirstSuggestion, String locationSecondSuggestion, String lastVisited) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "https://woop.activehosted.com/api/3/contact/sync";

        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Token", acToken);

        List<Map<String, Object>> fieldValues = new ArrayList<>();
        fieldValues.add(createFieldValueMap("293", firstSuggestion));
        fieldValues.add(createFieldValueMap("294", secondSuggestion));
        fieldValues.add(createFieldValueMap("295", firstSuggestionText));
        fieldValues.add(createFieldValueMap("296", secondSuggestionText));
        fieldValues.add(createFieldValueMap("297", firstSuggestionDifficulty));
        fieldValues.add(createFieldValueMap("298", secondSuggestionDifficulty));
        fieldValues.add(createFieldValueMap("299",erRetentionText));
        fieldValues.add(createFieldValueMap("300", locationFirstSuggestion));
        fieldValues.add(createFieldValueMap("301",locationSecondSuggestion));
        fieldValues.add(createFieldValueMap("302","0"));
        fieldValues.add(createFieldValueMap("303",lastVisited));


        Map<String, Object> contact = new HashMap<>();
        contact.put("email", email);
        contact.put("fieldValues", fieldValues);

        Map<String, Object> body = new HashMap<>();
        body.put("contact", contact);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(body);
        logger.info("Final JSON body sent: {}", json);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
        logger.info("API Response: {}", responseEntity.getBody());
    }

    @Override
    public void runQueryAndUpdate() throws JsonProcessingException {

        List<Object[]> result = erRepository.ERQuery();

        result.forEach(row -> {
            try {
                mapAndSendToAC(row);
            } catch (HttpServerErrorException.BadGateway e) {
                try {
                    Thread.sleep(5000);
                    mapAndSendToAC(row);

                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    private void mapAndSendToAC(Object[] row) {
        String contactEmail = row[0].toString();
        String lastVisited = row[1].toString();
        String text = row[2].toString();
        String firstSuggestionName = row[3].toString();
        String firstSuggestionLocation = row[4].toString();
        String firstSuggestionDifficulty = row[5].toString();
        String firstSuggestionText = row[6].toString();

        String secondSuggestionName;
        String secondSuggestionLocation;
        String secondSuggestionDifficulty;
        String secondSuggestionText;

        try {
            secondSuggestionName = row[7].toString();
            secondSuggestionLocation = row[8].toString();
            secondSuggestionDifficulty = row[9].toString();
            secondSuggestionText = row[10].toString();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            secondSuggestionDifficulty = null;
            secondSuggestionLocation = null;
            secondSuggestionName = null;
            secondSuggestionText = null;
        }

        try {
            updateACValues(contactEmail, firstSuggestionName, secondSuggestionName, firstSuggestionText, secondSuggestionText, firstSuggestionDifficulty, secondSuggestionDifficulty, text, firstSuggestionLocation, secondSuggestionLocation, lastVisited);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> createFieldValueMap(String fieldId, String value) {
        Map<String, Object> fieldValueMap = new HashMap<>();
        fieldValueMap.put("field", fieldId);
        fieldValueMap.put("value", value != null ? value : "");
        return fieldValueMap;
    }
}