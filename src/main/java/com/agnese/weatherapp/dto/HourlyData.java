package com.agnese.weatherapp.dto;

import lombok.Data; // Annotazione Lombok per getter, setter, costruttori, ecc.
import com.fasterxml.jackson.annotation.JsonProperty; // Per mappare i nomi JSON con underscore

import java.util.List;

/**
 * DTO (Data Transfer Object) per mappare la sezione "hourly" della risposta JSON di Open-Meteo.
 * Contiene liste di timestamp e temperature.
 */
@Data // Genera getter, setter, equals, hashCode, toString
public class HourlyData {
    private List<String> time; // Lista di timestamp (es. "2025-07-09T00:00")
    @JsonProperty("temperature_2m") // Mappa il campo JSON "temperature_2m" a temperature2m
    private List<Double> temperature2m; // Lista delle temperature a 2 metri
}

