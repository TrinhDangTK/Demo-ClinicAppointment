package com.clinicappointment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortalController {

    @GetMapping("/admin")
    public String adminPortal() {
        return "forward:/admin/doctors";
    }
}
