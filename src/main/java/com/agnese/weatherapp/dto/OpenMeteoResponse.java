package com.agnese.weatherapp.dto;

import lombok.Data; // Annotazione Lombok per getter, setter, equals, hashCode, toString
import com.fasterxml.jackson.annotation.JsonProperty; // Per mappare i nomi JSON con underscore

import java.util.List;

/**
 * DTO (Data Transfer Object) per mappare la risposta JSON principale dall'API Open-Meteo.
 * Utilizza Lombok per generare automaticamente getter e setter.
 */
@Data // Genera getter, setter, equals, hashCode, toString
public class OpenMeteoResponse {
    private double latitude;
    private double longitude;
    @JsonProperty("generationtime_ms") // Mappa il campo JSON "generationtime_ms" a generationTimeMs
    private double generationTimeMs;
    @JsonProperty("utc_offset_seconds")
    private int utcOffsetSeconds;
    private String timezone;
    @JsonProperty("timezone_abbreviation")
    private String timezoneAbbreviation;
    private double elevation;
    @JsonProperty("hourly_units")
    private HourlyUnits hourlyUnits; // DTO per le unità orarie
    private HourlyData hourly; // DTO per i dati orari

    // Classe interna per le unità orarie
    @Data
    public static class HourlyUnits {
        private String time;
        @JsonProperty("temperature_2m")
        private String temperature2m;
    }
}

