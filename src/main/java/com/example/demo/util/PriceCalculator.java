package com.example.demo.util;

import com.example.demo.dto.util.PriceBreakdownDto;
import com.example.demo.enums.CargoType;
import com.example.demo.enums.TrailerType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceCalculator {
    private static final double BASE_RATE = 500.0;

    public PriceBreakdownDto calculate(CargoType cargoType,
                                                TrailerType trailerType,
                                                double distanceKm,
                                                BigDecimal weightKg, boolean urgent) {
        // convert BigDecimal to double for calculation (handle null)
        double weightKgDouble = weightKg != null ? weightKg.doubleValue() : 0.0;

        double ratePerKm = switch (trailerType) {
            case FLATBED -> 20.0;
            case REFRIGERATED -> 30.0;
            case TANKER -> 25.0;
            case BULK_CARRIER -> 15.0;
            case BOX -> 10.0;
        };

        double distanceCost = distanceKm * ratePerKm;

        //Обрахунок вартості за вагу
        double weightCost = 0.0;
        if(weightKg != null) {
            if(weightKgDouble <= 1000){
                weightCost = weightKgDouble * 0.5;
            } else if (weightKgDouble <= 5000) {
                weightCost = 500 + (weightKgDouble  - 1000) * 0.3;
            } else {
                weightCost = 1700 + (weightKgDouble - 5000) * 0.2;
            }
        }

        //Основна вартість без урахування терміновості та інших критеріїв
        double basePrice = BASE_RATE + distanceCost + weightCost;

        //Множник за тип вантажу
        double cargoMultiplier = switch (cargoType) {
            case FRAGILE -> 1.5;
            case PERISHABLE -> 1.4;
            case GLASS -> 1.6;
            case LIQUID -> 1.3;
            case SOLID -> 1.2;
            case BULK -> 1.1;
        };

        //Множник за терміновість доставки
        double urgentMultiplier = urgent ? 1.25 : 1.0;

        //Фінальна вартість
        double finalPrice = basePrice * cargoMultiplier * urgentMultiplier;
        finalPrice = Math.round(finalPrice * 100.0) / 100.0;

        return PriceBreakdownDto.builder()
                .baseRate(BASE_RATE)
                .distanceKm(distanceKm)
                .perKmRate(ratePerKm)
                .distanceCost(Math.round(distanceCost * 100.0) / 100.0)
                .weightKg(weightKg)
                .weightCost(Math.round(weightCost * 100.0) / 100.0)
                .cargoMultiplier(cargoMultiplier)
                .urgentMultiplier(urgentMultiplier)
                .total(finalPrice)
                .build();
    }
}
