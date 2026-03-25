package com.clinicappointment.controller;

import com.clinicappointment.dto.DoctorForm;
import com.clinicappointment.model.Doctor;
import com.clinicappointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/doctors")
public class AdminDoctorController {

    private static final int PAGE_SIZE = 5;
    private final DoctorService doctorService;

    public AdminDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        int currentPage = Math.max(page, 0);
        Page<Doctor> doctorPage = doctorService.getDoctors(
            PageRequest.of(currentPage, PAGE_SIZE, Sort.by("id").ascending())
        );
        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("currentPage", currentPage);
        return "admin/doctors/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("doctorForm", new DoctorForm());
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("isEdit", false);
        model.addAttribute("doctorId", null);
        return "admin/doctors/form";
    }

    @PostMapping("/create")
    public String create(
        @Valid @ModelAttribute("doctorForm") DoctorForm doctorForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", doctorService.getAllDepartments());
            model.addAttribute("isEdit", false);
            model.addAttribute("doctorId", null);
            return "admin/doctors/form";
        }

        doctorService.createDoctor(doctorForm);
        redirectAttributes.addFlashAttribute("successMessage", "Tao bac si thanh cong");
        return "redirect:/admin/doctors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctorForm", doctorService.buildDoctorForm(doctor));
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("isEdit", true);
        model.addAttribute("doctorId", id);
        return "admin/doctors/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("doctorForm") DoctorForm doctorForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", doctorService.getAllDepartments());
            model.addAttribute("isEdit", true);
            model.addAttribute("doctorId", id);
            return "admin/doctors/form";
        }

        doctorService.updateDoctor(id, doctorForm);
        redirectAttributes.addFlashAttribute("successMessage", "Cap nhat bac si thanh cong");
        return "redirect:/admin/doctors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        doctorService.deleteDoctor(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xoa bac si thanh cong");
        return "redirect:/admin/doctors";
    }
}
