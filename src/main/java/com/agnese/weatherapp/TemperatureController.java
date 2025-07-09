package com.agnese.weatherapp;

import com.agnese.weatherapp.dto.HourlyData;
import com.agnese.weatherapp.dto.OpenMeteoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*") // Permette richieste da qualsiasi origine (per lo sviluppo)
public class TemperatureController {

    private static final Logger logger = LoggerFactory.getLogger(TemperatureController.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OPEN_METEO_API_URL = "https://api.open-meteo.com/v1/forecast";

    /**
     * Endpoint per recuperare i dati di temperatura da Open-Meteo.
     * @param latitude Latitudine della città.
     * @param longitude Longitudine della città.
     * @return Una mappa contenente le date e le temperature medie giornaliere.
     */
    @GetMapping("/api/temperatures")
    public Map<String, Object> getTemperatures(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        // Calcola la data di inizio per le ultime due settimane
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(13); // 14 giorni inclusi oggi

        // Formatta le date per l'API Open-Meteo
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        // Costruisci l'URL dell'API Open-Meteo
        String apiUrl = String.format(
                "%s?latitude=%.2f&longitude=%.2f&hourly=temperature_2m&start_date=%s&end_date=%s",
                OPEN_METEO_API_URL, latitude, longitude, formattedStartDate, formattedEndDate);

        Map<String, Object> result = new LinkedHashMap<>(); // LinkedHashMap per mantenere l'ordine

        try {
            // Effettua la chiamata all'API Open-Meteo e mappa direttamente al DTO
            OpenMeteoResponse response = restTemplate.getForObject(apiUrl, OpenMeteoResponse.class);

            if (response == null || response.getHourly() == null || response.getHourly().getTime() == null || response.getHourly().getTemperature2m() == null) {
                logger.warn("Risposta API Open-Meteo vuota o incompleta per lat: {}, lon: {}", latitude, longitude);
                result.put("error", "Nessun dato di temperatura valido ricevuto dall'API Open-Meteo.");
                return result;
            }

            HourlyData hourlyData = response.getHourly();
            List<String> rawTimes = hourlyData.getTime();
            List<Double> rawTemperatures = hourlyData.getTemperature2m();

            // Mappa per aggregare le temperature per giorno
            Map<LocalDate, List<Double>> dailyTemperatures = new LinkedHashMap<>();

            // Itera sui dati orari e raggruppa per giorno
            for (int i = 0; i < rawTimes.size(); i++) {
                String fullDateTime = rawTimes.get(i);
                LocalDate date = LocalDate.parse(fullDateTime.substring(0, 10)); // Estrai solo la data
                double temp = rawTemperatures.get(i);

                dailyTemperatures.computeIfAbsent(date, k -> new ArrayList<>()).add(temp);
            }

            List<String> dates = new ArrayList<>();
            List<Double> temperatures = new ArrayList<>();

            // Calcola la media giornaliera per ogni giorno e popola le liste di output
            for (Map.Entry<LocalDate, List<Double>> entry : dailyTemperatures.entrySet()) {
                LocalDate date = entry.getKey();
                List<Double> tempsOfDay = entry.getValue();

                double averageTemp = tempsOfDay.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(Double.NaN); // Gestisce il caso di lista vuota

                dates.add(date.format(formatter)); // Formatta la data per l'output
                temperatures.add(Math.round(averageTemp * 10.0) / 10.0); // Arrotonda a una cifra decimale
            }

            result.put("dates", dates);
            result.put("temperatures", temperatures);

        } catch (HttpClientErrorException e) {
            // Errori client (es. 4xx)
            logger.error("Errore client durante la chiamata all'API Open-Meteo: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            result.put("error", "Errore nella richiesta dati: " + e.getStatusCode().value() + ". Riprova più tardi.");
        } catch (HttpServerErrorException e) {
            // Errori server (es. 5xx)
            logger.error("Errore server dall'API Open-Meteo: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            result.put("error", "Il server meteo ha riscontrato un problema: " + e.getStatusCode().value() + ". Riprova più tardi.");
        } catch (Exception e) {
            // Altri errori (es. di rete, parsing JSON)
            logger.error("Errore generico durante il recupero dei dati di temperatura: {}", e.getMessage(), e);
            result.put("error", "Errore imprevisto durante il recupero dei dati. Controlla la console per dettagli.");
        }

        return result;
    }
}
