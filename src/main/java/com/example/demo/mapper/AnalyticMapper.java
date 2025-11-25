package com.example.demo.mapper;

import com.example.demo.dto.response.ComparePeriodsResponseDto;
import com.example.demo.dto.response.DynamicAnalyticDto;
import com.example.demo.dto.response.OrderStatusAnalyticResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class AnalyticMapper {

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(((Number) value).toString());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    public ComparePeriodsResponseDto mapCompareRow(Object[] row, int scaleMoney, int scalePercent) {
        String metric = row[0] != null ? row[0].toString() : null;

        BigDecimal currentValue = toBigDecimal(row.length > 1 ? row[1] : null).setScale(scaleMoney, BigDecimal.ROUND_HALF_UP);
        BigDecimal previousValue = toBigDecimal(row.length > 2 ? row[2] : null).setScale(scaleMoney, BigDecimal.ROUND_HALF_UP);

        BigDecimal change = currentValue.subtract(previousValue).setScale(scaleMoney, RoundingMode.HALF_UP);

        BigDecimal changePct = null;
        if (previousValue.compareTo(BigDecimal.ZERO) != 0) {
            // використовуємо більшу проміжну точність для ділення, потім округлюємо процент
            changePct = change
                    .divide(previousValue, scaleMoney + 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(scalePercent, RoundingMode.HALF_UP);
        }

        return ComparePeriodsResponseDto.builder()
                .metric(metric)
                .currentValue(currentValue)
                .previousValue(previousValue)
                .changeValue(change)
                .changePercentage(changePct)
                .build();
    }

    public OrderStatusAnalyticResponseDto mapOrderStatusRow(Object[] row, int scaleTotalRevenue, int scaleAvgOrderValue){
        String status = row[0] != null ? row[0].toString() : null;

        Long orderCount = (Long) row[1];
        BigDecimal percentage = (BigDecimal) row[2];
        BigDecimal totalRevenue = toBigDecimal(row.length > 3 ? row[3] : null).setScale(scaleTotalRevenue, BigDecimal.ROUND_HALF_UP);
        BigDecimal avgOrderValue = toBigDecimal(row.length > 4 ? row[4] : null).setScale(scaleAvgOrderValue, BigDecimal.ROUND_HALF_UP);

        return OrderStatusAnalyticResponseDto.builder()
                .status(status)
                .orderCount(orderCount)
                .percentage(percentage)
                .totalRevenue(totalRevenue)
                .avgOrderValue(avgOrderValue)
                .build();
    }

    public DynamicAnalyticDto mapToDynamicRow(Object[] row, int scaleAverageOrderValue){
        String period = row[0] != null ? row[0].toString() : null;

        Long orderCount = (Long) row[1];
        BigDecimal revenue = toBigDecimal(row[2]).setScale(scaleAverageOrderValue, BigDecimal.ROUND_HALF_UP);
        BigDecimal averageOrderValue = toBigDecimal(row[3]).setScale(scaleAverageOrderValue, BigDecimal.ROUND_HALF_UP);
        BigDecimal completedRate = (BigDecimal) row[4];

        return DynamicAnalyticDto.builder()
                .period(period)
                .orderCount(orderCount)
                .revenue(revenue)
                .averageOrderValue(averageOrderValue)
                .completedRate(completedRate)
                .build();
    }

    public
}
