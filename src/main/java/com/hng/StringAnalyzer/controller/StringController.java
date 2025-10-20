package com.hng.StringAnalyzer.controller;

import com.hng.StringAnalyzer.dto.AnalyzerResponse;
import com.hng.StringAnalyzer.dto.StringDto;
import com.hng.StringAnalyzer.model.StringEntity;
import com.hng.StringAnalyzer.service.AnalyzerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/strings")
public class StringController {

    private final AnalyzerService analyzerService;

    public StringController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping
    public ResponseEntity<?> analyzeString(@RequestBody StringDto request) {
        try {
            StringEntity stringEntity = analyzerService.analyzeAndSave(request);

            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put("length", stringEntity.getLength());
            properties.put("is_palindrome", stringEntity.isPalindrome());
            properties.put("unique_characters", stringEntity.getUniqueCharacters());
            properties.put("word_count", stringEntity.getWordCount());
            properties.put("sha256_hash", stringEntity.getId());
            properties.put("character_frequency_map", analyzerService.preserveOrder(stringEntity.getValue(), stringEntity.getCharacterFrequencyMap()));

            AnalyzerResponse response = new AnalyzerResponse();
            response.setId(stringEntity.getId());
            response.setValue(stringEntity.getValue());
            response.setProperties(properties);
            response.setCreated_at(stringEntity.getCreatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "String already exists"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", "Unprocessable entity"));
        }
    }

    @GetMapping("/{string_value}")
    public ResponseEntity<?> getStringDetails(@PathVariable("string_value") String value) throws Exception {
        try {
            StringEntity stringEntity = analyzerService.findByValue(value);

            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put("length", stringEntity.getLength());
            properties.put("is_palindrome", stringEntity.isPalindrome());
            properties.put("unique_characters", stringEntity.getUniqueCharacters());
            properties.put("word_count", stringEntity.getWordCount());
            properties.put("sha256_hash", stringEntity.getId());
            properties.put("character_frequency_map", analyzerService.preserveOrder(stringEntity.getValue(), stringEntity.getCharacterFrequencyMap()));

            AnalyzerResponse response = new AnalyzerResponse();
            response.setId(stringEntity.getId());
            response.setValue(stringEntity.getValue());
            response.setProperties(properties);
            response.setCreated_at(stringEntity.getCreatedAt());

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "String does not exist in the system"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", "Unprocessable entity"));
    }
}
}
