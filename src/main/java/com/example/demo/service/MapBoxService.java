package com.example.demo.service;

import com.example.demo.config.MapBoxConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.geocoding.v6.MapboxV6Geocoding;
import com.mapbox.api.geocoding.v6.V6ForwardGeocodingRequestOptions;
import com.mapbox.api.geocoding.v6.models.V6Feature;
import com.mapbox.api.geocoding.v6.models.V6Response;
import com.mapbox.geojson.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MapBoxService {
    private final MapBoxConfig mapBoxConfig;

    public MapBoxService(MapBoxConfig mapBoxConfig) {
        this.mapBoxConfig = mapBoxConfig;
    }

    public Point geocodeAddress(String address) {
        V6ForwardGeocodingRequestOptions requestOptions = V6ForwardGeocodingRequestOptions.builder(address)
                .autocomplete(false)
                .language("en")
                .types("address", "street", "place")
                .build();

        MapboxV6Geocoding mapboxGeocoding = MapboxV6Geocoding.builder(
                mapBoxConfig.getAccessToken(),
                requestOptions
        ).build();

        try {
            Response<V6Response> response = mapboxGeocoding.executeCall();
            if (response.isSuccessful() && response.body() != null) {
                List<V6Feature> results = response.body().features();

                log.debug(results.toString());

                if (results != null && !results.isEmpty()) {
                    Point firstResultPoint = (Point) results.get(0).geometry();

                    log.info("Coordinates received from geocodeAddress() {}", firstResultPoint);
                    return firstResultPoint; // отримуємо респонс у форматі [longitude, latitude] !!!
                }
            }
            throw new IllegalArgumentException("No geocoding results found for address: " + address);
        } catch (IOException e) {
            throw new RuntimeException("Geocoding request failed: " + e.getMessage(), e);
        }
    }

    public DirectionsRoute getRoute(Point origin, Point destination) {
        log.info("Requesting route from [lon: {}, lat: {}] to [lon: {}, lat: {}]",
                origin.longitude(), origin.latitude(),
                destination.longitude(), destination.latitude());

        RouteOptions routeOptions = RouteOptions.builder()
                .coordinatesList(Arrays.asList(origin, destination))
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE)  // Використовуємо encoded polyline
                .steps(true)
                .build();

        MapboxDirections directionsClient = MapboxDirections.builder()
                .routeOptions(routeOptions)
                .accessToken(mapBoxConfig.getAccessToken())
                .build();

        try {
            Response<DirectionsResponse> response = directionsClient.executeCall();
            if (response.isSuccessful() && response.body() != null) {
                List<DirectionsRoute> routes = response.body().routes();
                if (routes != null && !routes.isEmpty()) {
                    DirectionsRoute route = routes.get(0);

                    log.info("Route found: distance={} m, duration={} s",
                            route.distance(), route.duration());

                    // Geometry буде в форматі encoded polyline string
                    log.info("Polyline geometry: {}", route.geometry());

                    return route;
                }
            }
            throw new IllegalArgumentException("No route found between points");
        } catch (IOException e) {
            throw new RuntimeException("Directions request failed: " + e.getMessage(), e);
        }
    }

    public String getEncodedPolyline(DirectionsRoute route) {
        return route.geometry();  // Повертає encoded polyline string
    }

    public void testRoute(String originAddress, String destinationAddress) {
        log.info("Testing route from '{}' to '{}'", originAddress, destinationAddress);

        Point origin = geocodeAddress(originAddress);
        Point destination = geocodeAddress(destinationAddress);

        log.info("Origin coordinates: {}", origin);
        log.info("Destination coordinates: {}", destination);

        DirectionsRoute route = getRoute(origin, destination);

        log.info("Route distance: {} meters, duration: {} seconds",
                route.distance(), route.duration());
    }









//    public RouteInfo getRoute(Point origin, Point destination) {
//        String url = String.format(Locale.US,
//                "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f?geometries=polyline&overview=full&access_token=%s",
//                origin.longitude(), origin.latitude(),
//                destination.longitude(), destination.latitude(),
//                mapBoxConfig.getAccessToken()
//        );
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        try {
//            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
//            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
//                throw new RuntimeException("Directions request failed: HTTP " + resp.getStatusCodeValue());
//            }
//
//            JsonNode root = MAPPER.readTree(resp.getBody());
//            JsonNode routes = root.path("routes");
//            if (!routes.isArray() || routes.size() == 0) {
//                throw new IllegalArgumentException("No route found between points");
//            }
//            JsonNode first = routes.get(0);
//            double distanceMeters = first.path("distance").asDouble(0.0);
//            double durationSeconds = first.path("duration").asDouble(0.0);
//            String geometry = first.path("geometry").asText(null); // encoded polyline
//
//            return new RouteInfo(distanceMeters, durationSeconds, geometry);
//        } catch (Exception e) {
//            throw new RuntimeException("Directions request failed: " + e.getMessage(), e);
//        }
//    }
}
