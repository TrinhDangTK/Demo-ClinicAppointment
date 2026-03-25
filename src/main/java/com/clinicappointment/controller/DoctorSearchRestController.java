package com.clinicappointment.controller;

import com.clinicappointment.dto.DoctorCardDto;
import com.clinicappointment.dto.DoctorSearchResponse;
import com.clinicappointment.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
public class DoctorSearchRestController {

    private static final int PAGE_SIZE = 5;
    private final DoctorService doctorService;

    public DoctorSearchRestController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<DoctorSearchResponse> search(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page
    ) {
        int currentPage = Math.max(page, 0);
        Page<DoctorCardDto> doctorPage = doctorService.getDoctorCards(
            keyword,
            PageRequest.of(currentPage, PAGE_SIZE, Sort.by("id").ascending())
        );
        DoctorSearchResponse body = new DoctorSearchResponse(
            doctorPage.getContent(),
            doctorPage.getNumber(),
            doctorPage.getTotalPages(),
            doctorPage.getTotalElements(),
            doctorPage.isFirst(),
            doctorPage.isLast()
        );
        return ResponseEntity.ok(body);
    }
}
