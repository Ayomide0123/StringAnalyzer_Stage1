package com.hng.StringAnalyzer.utils;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HelperMethods {
    public Map<String, Object> parseNaturalLanguage(String query) {
        query = query.toLowerCase();
        Map<String, Object> filters = new HashMap<>();

        if (query.contains("palindromic")) filters.put("is_palindrome", true);
        if (query.contains("single word") || query.contains("one word")) filters.put("word_count", 1);
        if (query.contains("containing the letter")) {
            char c = query.charAt(query.lastIndexOf("letter") + 7);
            filters.put("contains_character", String.valueOf(c));
        } else if (query.contains("containing the vowel") || query.contains("first vowel")) {
            filters.put("contains_character", "a");
        } else if (query.matches(".*contain[s]? the letter [a-z].*")) {
            char c = query.replaceAll(".*contain[s]? the letter ([a-z]).*", "$1").charAt(0);
            filters.put("contains_character", String.valueOf(c));
        }

        if (query.matches(".*longer than \\d+ characters.*")) {
            int minLength = Integer.parseInt(query.replaceAll(".*longer than (\\d+) characters.*", "$1")) + 1;
            filters.put("min_length", minLength);
        }

        if (filters.containsKey("word_count") && (int) filters.get("word_count") < 0) {
            throw new IllegalStateException("Conflicting or invalid word count filters.");
        }

        if (filters.isEmpty()) {
            throw new IllegalArgumentException("Unable to parse natural language query.");
        }

        return filters;
    }
}
