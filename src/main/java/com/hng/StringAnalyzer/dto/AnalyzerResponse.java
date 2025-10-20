package com.hng.StringAnalyzer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class AnalyzerResponse {
    private String id;
    private String value;
    private Map<String, Object> properties;
    private LocalDateTime created_at;
}
