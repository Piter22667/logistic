package com.example.demo.service;

import com.example.demo.enums.CargoType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class PricingService {

    // Базова ставка за кілометр
    private static final BigDecimal BASE_RATE_PER_KM = new BigDecimal("15.00");

    // Мінімальна вартість замовлення
    private static final BigDecimal MIN_COST = new BigDecimal("500.00");

    /**
     * Розрахунок вартості перевезення
     *
     * Формула:
     * Base Cost = Distance × Base Rate
     * Cargo Multiplier = залежить від типу вантажу
     * Weight Multiplier = залежить від ваги
     * Final Cost = Base Cost × Cargo Multiplier × Weight Multiplier
     */
    public BigDecimal calculateCost(
            CargoType cargoType,
            BigDecimal weightKg,
            BigDecimal distanceKm) {

        log.debug("Calculating cost: type={}, weight={}, distance={}",
                cargoType, weightKg, distanceKm);

        // 1. Базова вартість
        BigDecimal baseCost = distanceKm.multiply(BASE_RATE_PER_KM);

        // 2. Множник за тип вантажу
        BigDecimal cargoMultiplier = getCargoTypeMultiplier(cargoType);

        // 3. Множник за вагу
        BigDecimal weightMultiplier = getWeightMultiplier(weightKg);

        // 4. Фінальна вартість
        BigDecimal finalCost = baseCost
                .multiply(cargoMultiplier)
                .multiply(weightMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        // 5. Мінімальна вартість
        if (finalCost.compareTo(MIN_COST) < 0) {
            finalCost = MIN_COST;
        }

        log.info("Calculated cost: {} (base={}, cargo={}x, weight={}x)",
                finalCost, baseCost, cargoMultiplier, weightMultiplier);

        return finalCost;
    }

    private BigDecimal getCargoTypeMultiplier(CargoType cargoType) {
        return switch (cargoType) {
            case LIQUID -> new BigDecimal("1.20");      // +20% (небезпечний)
            case FRAGILE -> new BigDecimal("1.50");     // +50% (обережна їзда)
            case PERISHABLE -> new BigDecimal("1.40");  // +40% (термінова доставка)
            case BULK -> new BigDecimal("1.10");        // +10% (складне завантаження)
            case SOLID -> BigDecimal.ONE;               // Без надбавки
            case GLASS -> BigDecimal.ONE;
        };
    }

    private BigDecimal getWeightMultiplier(BigDecimal weightKg) {
        // До 10 тонн — без надбавки
        if (weightKg.compareTo(new BigDecimal("10000")) <= 0) {
            return BigDecimal.ONE;
        }

        // 10-20 тонн — +10%
        if (weightKg.compareTo(new BigDecimal("20000")) <= 0) {
            return new BigDecimal("1.10");
        }

        // 20-30 тонн — +20%
        if (weightKg.compareTo(new BigDecimal("30000")) <= 0) {
            return new BigDecimal("1.20");
        }

        // Більше 30 тонн — +30%
        return new BigDecimal("1.30");
    }
}