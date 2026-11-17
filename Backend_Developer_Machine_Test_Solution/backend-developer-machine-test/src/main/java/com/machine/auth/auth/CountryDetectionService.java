package com.machine.auth.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class CountryDetectionService {
    private static final Pattern IP_ADDRESS = Pattern.compile("(?i)^[0-9a-f:.]+$");
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    public String countryFor(HttpServletRequest request, String localDevelopmentCountry) {
        String remoteAddress = request.getRemoteAddr();
        String clientAddress = remoteAddress;

        // Express runs as a local reverse proxy. Only trust its forwarded IP when the direct peer is loopback.
        if (isLoopback(remoteAddress)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) clientAddress = forwarded.split(",")[0].trim();
        }

        if (isLoopback(clientAddress)) {
            if (localDevelopmentCountry == null || !localDevelopmentCountry.matches("(?i)^[a-z]{2}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a two-letter country code for local signup, e.g. IN");
            }
            return localDevelopmentCountry.toUpperCase(Locale.ROOT);
        }

        if (clientAddress.regionMatches(true, 0, "::ffff:", 0, 7)) clientAddress = clientAddress.substring(7);
        if (clientAddress.contains("%")) clientAddress = clientAddress.substring(0, clientAddress.indexOf('%'));
        if (!IP_ADDRESS.matcher(clientAddress).matches()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Could not determine signup country");
        }
        try {
            URI uri = URI.create("https://ipapi.co/" + clientAddress + "/country/");
            HttpRequest geoRequest = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(4))
                .header("User-Agent", "backend-developer-machine-test/1.0")
                .GET().build();
            HttpResponse<String> response = http.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            String country = response.body().trim();
            if (response.statusCode() != 200 || !country.matches("(?i)^[a-z]{2}$")) {
                throw new IllegalStateException("GeoIP provider returned no country");
            }
            return country.toUpperCase(Locale.ROOT);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Could not determine signup country. Please try again.", ex);
        }
    }

    private boolean isLoopback(String address) {
        try {
            return InetAddress.getByName(address).isLoopbackAddress();
        } catch (Exception ignored) {
            return false;
        }
    }
}
