package com.clinicappointment.controller;

import com.clinicappointment.dto.DoctorCardDto;
import com.clinicappointment.service.DoctorService;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private static final int PAGE_SIZE = 5;
    private final DoctorService doctorService;

    public HomeController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping({"/", "/courses"})
    public String home(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "") String keyword,
        Model model,
        Authentication authentication
    ) {
        int currentPage = Math.max(page, 0);
        Page<DoctorCardDto> doctorPage = doctorService.getDoctorCards(
            keyword,
            PageRequest.of(currentPage, PAGE_SIZE, Sort.by("id").ascending())
        );
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
            .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_ADMIN"));
        boolean isPatient = isAuthenticated && authentication.getAuthorities().stream()
            .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_PATIENT"));

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isPatient", isPatient);
        model.addAttribute("username", isAuthenticated ? authentication.getName() : "");
        return "home";
    }
}
