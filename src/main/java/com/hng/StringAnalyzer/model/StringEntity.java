package com.hng.StringAnalyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "strings")
@Getter
@Setter
public class StringEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;

    private int length;
    private boolean isPalindrome;
    private int uniqueCharacters;
    private int wordCount;

    @ElementCollection
    @CollectionTable(name = "char_frequency", joinColumns = @JoinColumn(name="string_id"))
    @MapKeyColumn(name = "character_value")
    @Column(name = "count_value")
    private Map<Character, Integer> characterFrequencyMap;

    private LocalDateTime createdAt;
}
