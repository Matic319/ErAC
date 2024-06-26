package com.maticz.ER.AC.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface ACService {

     void updateACValues(String email, String firstSuggestion, String secondSuggestion, String firstSuggestionText, String secondSuggestionText, String firstSuggestionDifficulty,
                               String secondSuggestionDifficulty, String erRetentionText, String locationFirstSuggestion, String locationSecondSuggestion, String lastVisited) throws JsonProcessingException;

     void runQueryAndUpdate() throws JsonProcessingException;
}
