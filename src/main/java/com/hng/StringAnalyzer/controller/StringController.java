package com.hng.StringAnalyzer.controller;

import com.hng.StringAnalyzer.dto.AnalyzerResponse;
import com.hng.StringAnalyzer.dto.StringDto;
import com.hng.StringAnalyzer.model.StringEntity;
import com.hng.StringAnalyzer.service.AnalyzerService;
import com.hng.StringAnalyzer.utils.HelperMethods;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/strings")
public class StringController {

    private final AnalyzerService analyzerService;
    private HelperMethods helperMethods;

    public StringController(AnalyzerService analyzerService, HelperMethods helperMethods) {
        this.analyzerService = analyzerService;
        this.helperMethods = helperMethods;
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


    @GetMapping
    public ResponseEntity<?> getAllStrings(
            @RequestParam(required = false) Boolean is_palindrome,
            @RequestParam(required = false) Integer min_length,
            @RequestParam(required = false) Integer max_length,
            @RequestParam(required = false) Integer word_count,
            @RequestParam(required = false) String contains_character
    ) {
      try {
          var results = analyzerService.getAllStringsFiltered(is_palindrome, min_length, max_length, word_count, contains_character);

          Map<String, Object> response = new LinkedHashMap<>();
          response.put("data", results.get("data"));
          response.put("count", results.get("count"));

          Map<String, Object> filtersApplied = new LinkedHashMap<>();
          if (is_palindrome !=null) filtersApplied.put("is_palindrome", is_palindrome);
          if (min_length !=null) filtersApplied.put("min_length", min_length);
          if (max_length !=null) filtersApplied.put("max_length", max_length);
          if (word_count !=null) filtersApplied.put("word_count", word_count);
          if (contains_character !=null) filtersApplied.put("contains_character", contains_character);

          return ResponseEntity.ok(response);
      } catch (IllegalArgumentException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", "Unprocessable entity"));
      }
    }


    @GetMapping("/filter-by-natural-language")
    public ResponseEntity<?> filterByNaturalLanguage(@RequestParam String query) {
        try {
            Map<String, Object> parsedFilters = helperMethods.parseNaturalLanguage(query);

            List<StringEntity> results = analyzerService.filterStrings(parsedFilters);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", results);
            response.put("count", results.size());
            response.put("interpreted_query", Map.of(
                    "original", query,
                    "parsed_filters", parsedFilters
            ));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", e.getMessage()));
        }
    }
}
