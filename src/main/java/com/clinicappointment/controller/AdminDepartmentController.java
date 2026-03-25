package com.clinicappointment.controller;

import com.clinicappointment.dto.DepartmentForm;
import com.clinicappointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/departments")
public class AdminDepartmentController {

    private final DoctorService doctorService;

    public AdminDepartmentController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("departmentForm", new DepartmentForm());
        return "admin/departments/form";
    }

    @PostMapping("/create")
    public String create(
        @Valid @ModelAttribute("departmentForm") DepartmentForm departmentForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/departments/form";
        }

        try {
            doctorService.createDepartment(departmentForm);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/departments/form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Thêm khóa thnahf công");
        return "redirect:/admin/doctors";
    }
}
