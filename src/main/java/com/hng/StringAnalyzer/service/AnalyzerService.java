package com.hng.StringAnalyzer.service;

import com.hng.StringAnalyzer.dto.StringDto;
import com.hng.StringAnalyzer.model.StringEntity;
import com.hng.StringAnalyzer.repository.StringRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AnalyzerService {
    private final StringRepository stringRepository;

    public AnalyzerService(StringRepository stringRepository) {
        this.stringRepository = stringRepository;
    }

    public StringEntity analyzeAndSave(StringDto request) throws Exception {
        String value = request.getValue();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing or empty 'value' field");
        }

        String hash = computeSha256(value);

        if (stringRepository.existsById(hash)) {
            throw new DataIntegrityViolationException("String already exists");
        }

        StringEntity stringEntity = new StringEntity();
        stringEntity.setId(hash);
        stringEntity.setValue(value);
        stringEntity.setLength(value.length());
        stringEntity.setPalindrome(isPalindrome(value));
        stringEntity.setUniqueCharacters(countUniqueCharacters(value));
        stringEntity.setWordCount(countWords(value));
        stringEntity.setCharacterFrequencyMap(getCharacterFrequency(value));
        stringEntity.setCreatedAt(LocalDateTime.now());

        return stringRepository.save(stringEntity);
    }

    private String computeSha256(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes)
            hexString.append(String.format("%02x", b));
        return hexString.toString();
    }

    private boolean isPalindrome(String text){
        String cleaned = text.replaceAll("\\s+", "").toLowerCase();
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    private int countUniqueCharacters(String text) {
        return (int) text.chars()
                .mapToObj(c -> (char) c)
                .map(Character::toLowerCase)
                .distinct()
                .count();
    }

    private int countWords(String text) {
        String[] words = text.trim().split("\\s+");
        return words.length;
    }

    private Map<Character, Integer> getCharacterFrequency(String text) {
        Map<Character, Integer> freq = new LinkedHashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }
}
