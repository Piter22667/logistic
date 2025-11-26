package com.example.demo.service;

import com.example.demo.dto.response.*;
import com.example.demo.entity.AnalyticsSnapshot;
import com.example.demo.enums.PeriodType;
import com.example.demo.mapper.AnalyticMapper;
import com.example.demo.repository.AnalyticSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticService {

    private final AnalyticSnapshotRepository analyticSnapshotRepository;
    private final AnalyticMapper analyticMapper;
    private final ObjectMapper objectMapper;

    public AnalyticService(AnalyticSnapshotRepository analyticSnapshotRepository, AnalyticMapper analyticMapper, ObjectMapper objectMapper) {
        this.analyticSnapshotRepository = analyticSnapshotRepository;
        this.analyticMapper = analyticMapper;
        this.objectMapper = objectMapper;
    }

    public MonthlyMetricsResponseDto getLatestMonthlyMetrics() {
        AnalyticsSnapshot snapshot = analyticSnapshotRepository.findLatestMonthlyRevenue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analytic was not found"));

        Map<String, Object> metrics = snapshot.getMetrics();

        return MonthlyMetricsResponseDto.builder()
                .year(getIntValue(metrics, "year"))
                .month(getIntValue(metrics, "month"))
                .totalOrders(getLongValue(metrics, "total_orders"))
                .completedOrders(getLongValue(metrics, "completed_orders"))
                .cancelledOrders(getLongValue(metrics, "cancelled_orders"))
                .totalRevenue(getBigDecimalValue(metrics, "total_revenue"))
                .avgOrderValue(getBigDecimalValue(metrics, "avg_order_value"))
                .totalDistanceKm(getBigDecimalValue(metrics, "total_distance_km"))
                .build();
    }

    private Integer getIntValue(Map<String, Object> metrics, String key) {
        Object value = metrics.get(key);
        return value != null ? ((Number) value).intValue() : null;
    }

    private Long getLongValue(Map<String, Object> metrics, String key) {
        Object value = metrics.get(key);
        return value != null ? ((Number) value).longValue() : null;
    }

    private BigDecimal getBigDecimalValue(Map<String, Object> metrics, String key) {
        Object value = metrics.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }

    public List<OrderStatusAnalyticResponseDto> getOrderStatusAnalytics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = analyticSnapshotRepository.getOrderStatusAnalytics(startDate, endDate);

        if (results == null || results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Analytics not found");
        }

        return results.stream().map(
                        row -> analyticMapper.mapOrderStatusRow(row, 2, 3)).toList();
    }

    public List<DynamicAnalyticDto> getDynamicStatistic(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        List<Object[]> results = analyticSnapshotRepository.getDynamicRevenueAnalytics(startDate, endDate, periodType.getSqlValue());

        if (results == null || results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dynamic analytic not found");
        }

        return results.stream()
                .map(row -> analyticMapper.mapToDynamicRow(row, 2))
                .toList();
    }

    public List<ComparePeriodsResponseDto> comparePeriods(LocalDate currentStart, LocalDate currentEnd, LocalDate previousStart, LocalDate previousEnd) {
        List<Object[]> comparedPeriods = analyticSnapshotRepository.comparePeriods(currentStart, currentEnd, previousStart, previousEnd);

        if (comparedPeriods == null || comparedPeriods.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compare periods not found");
        }

        return comparedPeriods.stream()
                .map(row -> analyticMapper.mapCompareRow(row, 2, 2))
                .toList();
    }
}
