package com.onat.jurist.lawyer.controller;

import com.onat.jurist.lawyer.dto.out.LawyerResponseRateDTO;
import com.onat.jurist.lawyer.dto.out.LawyerWorkloadDTO;
import com.onat.jurist.lawyer.service.AvocatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/lawyers")
@RequiredArgsConstructor
public class LawyerStatsController {

    private final AvocatService avocatService;

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalLawyers() {
        return ResponseEntity.ok(avocatService.countLawyers());
    }

    @GetMapping("/active")
    public ResponseEntity<Long> getActiveLawyers() {
        return ResponseEntity.ok(avocatService.countActiveLawyers());
    }

    @GetMapping("/workload")
    public ResponseEntity<List<LawyerWorkloadDTO>> getLawyerWorkload() {
        return ResponseEntity.ok(avocatService.getLawyerWorkload());
    }

    @GetMapping("/response-rate")
    public ResponseEntity<List<LawyerResponseRateDTO>> getResponseRates() {
        return ResponseEntity.ok(avocatService.getLawyerResponseRates());
    }

    @GetMapping("/lawyer-utilization")
    public ResponseEntity<List<LawyerWorkloadDTO>> getLawyerUtilization() {
        return ResponseEntity.ok(avocatService.getLawyerWorkload());
    }

    @GetMapping("/region/lawyers")
    public ResponseEntity<Map<String, Long>> getLawyersByRegion() {
        return ResponseEntity.ok(avocatService.countLawyersByRegion());
    }

    @GetMapping("/trends")
    public ResponseEntity<Map<Integer, Long>> getNewLawyersPerYear() {
        return ResponseEntity.ok(avocatService.countLawyersPerYear());
    }


}