package com.example.demo.controller;

import com.example.demo.dto.response.*;
import com.example.demo.enums.PeriodType;
import com.example.demo.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticService analyticsService;

    @GetMapping("/monthly-metrics/latest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlyMetricsResponseDto> getLatestMonthlyMetrics() {
        return ResponseEntity.ok(analyticsService.getLatestMonthlyMetrics());
    }

    @GetMapping("/order-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderStatusAnalyticResponseDto>> getOrderStatusAnalytic(
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate
    ) {
        List<OrderStatusAnalyticResponseDto> orderStatusAnalytic = analyticsService.getOrderStatusAnalytics(startDate, endDate);
        return ResponseEntity.ok(orderStatusAnalytic);
    }

    @GetMapping("/dynamic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DynamicAnalyticDto>> getDynamicStatistic(
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate,
            @RequestParam(name = "periodType", defaultValue = "DAY") PeriodType periodType
    ) {
        List<DynamicAnalyticDto> dynamicStatistic = analyticsService.getDynamicStatistic(startDate, endDate, periodType);
        return ResponseEntity.ok(dynamicStatistic);
    }

    @GetMapping("/compare")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComparePeriodsResponseDto>> comparePeriods(
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate currentStart,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate currentEnd,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate previousStart,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate previousEnd
    ){
        List<ComparePeriodsResponseDto> comparePeriods = analyticsService.comparePeriods(currentStart, currentEnd, previousStart, previousEnd);
        return ResponseEntity.ok(comparePeriods);
    }
}