package com.example.demo.repository;

import com.example.demo.entity.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnalyticSnapshotRepository extends JpaRepository<AnalyticsSnapshot, Long> {

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.snapshotType = 'monthly_revenue'" +
            " ORDER BY a.snapshotDate DESC LIMIT 1")
    Optional<AnalyticsSnapshot> findLatestMonthlyRevenue();

    @Query(value = "SELECT * FROM get_order_status_analytics(:startDate, :endDate)", nativeQuery = true)
    List<Object[]> getOrderStatusAnalytics(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);


    @Query(value = "SELECT * FROM get_dynamic_stats(:startDate, :endDate, :groupBy)", nativeQuery = true)
    List<Object[]> getDynamicRevenueAnalytics(@Param("startDate") LocalDate startDay,
                                              @Param("endDate") LocalDate endDay,
                                              @Param("groupBy") String groupBy);



    @Query(value = "SELECT * FROM compare_periods(:p_current_start, :p_current_end, :p_previous_start, :p_previous_end)", nativeQuery = true)
    List<Object[]> comparePeriods(@Param("p_current_start") LocalDate currentStart,
                                  @Param("p_current_end") LocalDate currentEnd,
                                  @Param("p_previous_start") LocalDate previousStart,
                                  @Param("p_previous_end") LocalDate previousEnd);
}
