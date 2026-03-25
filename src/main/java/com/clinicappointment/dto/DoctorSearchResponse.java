package com.clinicappointment.dto;

import java.util.List;

public record DoctorSearchResponse(
    List<DoctorCardDto> content,
    int currentPage,
    int totalPages,
    long totalElements,
    boolean first,
    boolean last
) {
}
