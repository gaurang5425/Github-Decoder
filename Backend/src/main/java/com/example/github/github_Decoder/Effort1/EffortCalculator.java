package com.example.github.github_Decoder.Effort1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class EffortCalculator {
    private final Map<String, Double> languageEffortWeights = new HashMap<>() {{
        put("Java", 2.0);
        put("Python", 1.5);
        put("JavaScript", 1.8);
        put("C++", 2.2);
        put("C", 2.0);
        put("HTML", 1.0);
        put("CSS", 1.2);
        put("SQL", 1.5);
        put("Go", 1.7);
        put("Ruby", 1.6);
        put("PHP", 1.7);
        put("TypeScript", 1.9);
        put("Swift", 2.1);
        put("Kotlin", 1.9);
        put("Rust", 2.1);
    }};
    
    public double calculateEffortHours(String language, int fileCount) {
        double weight = languageEffortWeights.getOrDefault(language, 1.5);
        return Math.round(fileCount * weight * 100.0) / 100.0;
    }
}
