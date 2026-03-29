package com.onat.jurist.lawyer.controller;

import com.onat.jurist.lawyer.dto.in.AvocatRequest;
import com.onat.jurist.lawyer.dto.out.AvocatResponse;
import com.onat.jurist.lawyer.exception.ResourceNotFoundException;
import com.onat.jurist.lawyer.service.AvocatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lawyers")
@RequiredArgsConstructor
public class AvocatController {

    private final AvocatService avocatService;


    @PostMapping("/create")
    public ResponseEntity<AvocatResponse> createAvocat(@Valid @RequestBody AvocatRequest request) {
        AvocatResponse created = avocatService.createAvocat(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/get-all")
    public ResponseEntity<List<AvocatResponse>> getAllAvocats() {
        List<AvocatResponse> avocats = avocatService.getAllAvocats();
        return ResponseEntity.ok(avocats);
    }


    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<AvocatResponse> getAvocatById(@PathVariable Long id) {
        return avocatService.getAvocatById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Avocat non trouvé avec l'id: " + id));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<AvocatResponse> updateAvocat(@PathVariable Long id,
                                                       @RequestBody AvocatRequest request) {
        AvocatResponse updated = avocatService.updateAvocat(id, request);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAvocat(@PathVariable Long id) {
        avocatService.deleteAvocat(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] bytes = avocatService.exportAvocatsToExcel();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=avocats.xlsx")
                .body(bytes);
    }

    @GetMapping("/export/pdf/design")
    public ResponseEntity<byte[]> exportPdfDesign() {
        byte[] bytes = avocatService.exportAvocatsToPdfWithDesign();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=avocats.pdf")
                .body(bytes);
    }



}
