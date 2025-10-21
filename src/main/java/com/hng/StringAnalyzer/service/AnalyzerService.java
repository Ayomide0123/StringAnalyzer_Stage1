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
import java.util.stream.Stream;

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

    public StringEntity findByValue(String value) throws Exception {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing or epmty string value");
        }

        String hash = computeSha256(value);
        return stringRepository.findById(hash)
                .orElseThrow(() -> new NoSuchElementException("String not found"));
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

    public Map<Character, Integer> preserveOrder(String text, Map<Character, Integer> storedMap) {
        Map<Character, Integer> ordered = new LinkedHashMap<>();
        for (char c : text.toCharArray()) {
            if (!ordered.containsKey(c) && storedMap.containsKey(c)) {
                ordered.put(c, storedMap.get(c));
            }
        }
        return ordered;
    }

    public Map<String, Object> getAllStringsFiltered(
            Boolean isPalindrome,
            Integer minLength,
            Integer maxLength,
            Integer wordCount,
            String containersCharacter
    ) {
        List<StringEntity> all = stringRepository.findAll();

        if (containersCharacter != null && containersCharacter.length() != 1) {
            throw new IllegalArgumentException("contains_character must be a single character");
        }

        Stream<StringEntity> stream = all.stream();

        if (isPalindrome != null) {
            stream = stream.filter(s ->s.isPalindrome() == isPalindrome);
        }

        if (minLength != null) {
            stream = stream.filter(s ->s.getLength() >= minLength);
        }

        if (maxLength != null) {
            stream = stream.filter(s ->s.getLength() <= maxLength);
        }

        if (wordCount != null) {
            stream = stream.filter(s ->s.getWordCount() == wordCount);
        }

        if (containersCharacter != null) {
            char ch = containersCharacter.charAt(0);
            stream = stream.filter(s ->s.getValue().indexOf(ch) != -1);
        }

        List<Map<String, Object>> dataList = stream.map(entity -> {
            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put("length", entity.getLength());
            properties.put("is_palindrome", entity.isPalindrome());
            properties.put("unique_characters", entity.getUniqueCharacters());
            properties.put("word_count", entity.getWordCount());
            properties.put("sha256_hash", entity.getId());
            properties.put("character_frequency_map", entity.getCharacterFrequencyMap());

            Map<String, Object> responseItem = new LinkedHashMap<>();
            responseItem.put("id", entity.getId());
            responseItem.put("value", entity.getValue());
            responseItem.put("properties", properties);
            responseItem.put("created_at", entity.getCreatedAt());

            return responseItem;
        }).toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", dataList);
        result.put("count", dataList.size());

        return result;
    }
}
