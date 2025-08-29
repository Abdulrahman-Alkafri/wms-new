package com.example.wmsnew.statistics;

import com.example.wmsnew.statistics.dto.AnalyticsDto;
import com.example.wmsnew.statistics.dto.BusinessComparisonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DashboardStatistics> getDashboardStatistics() {
        DashboardStatistics statistics = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AnalyticsDto.AnalyticsResponse> getAnalytics(
            @RequestParam(defaultValue = "month") String period) {
        AnalyticsDto.AnalyticsResponse analytics = statisticsService.getAnalytics(period);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/business-comparison")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BusinessComparisonDto.BusinessComparisonResponse> getBusinessComparison(
            @RequestParam(defaultValue = "month") String period) {
        BusinessComparisonDto.BusinessComparisonResponse comparison = statisticsService.getBusinessComparison(period);
        return ResponseEntity.ok(comparison);
    }
}
