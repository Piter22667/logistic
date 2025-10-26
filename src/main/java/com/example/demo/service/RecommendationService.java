package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {
    private static final Map<String, List<String>> recommendationList = Map.of(
            "LIQUID", List.of("TANKER"),
            "BULK", List.of("BULK_CARRIER"),
            "SOLID", List.of("FLATBED", "BOX"),
            "FRAGILE", List.of("BOX"),
            "PERISHABLE", List.of("REFRIGERATED"),
            "HAZARDOUS", List.of("TANKER")
    );

    public List<String> getTrailerByCargoType(String cargoType){
        return recommendationList.getOrDefault(cargoType.toUpperCase(), List.of("BOX"));
    }
}
