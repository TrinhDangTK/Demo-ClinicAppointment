package com.clinicappointment.controller;

import com.clinicappointment.dto.AdminAppointmentForm;
import com.clinicappointment.model.Appointment;
import com.clinicappointment.model.AppointmentStatus;
import com.clinicappointment.service.AppointmentService;
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
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    private static final int PAGE_SIZE = 10;
    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        int currentPage = Math.max(page, 0);
        Page<Appointment> appointmentPage = appointmentService.getAllAppointments(
            PageRequest.of(currentPage, PAGE_SIZE, Sort.by("id").descending())
        );
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("currentPage", currentPage);
        return "admin/appointments/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("appointmentForm", new AdminAppointmentForm());
        populateOptions(model);
        model.addAttribute("isEdit", false);
        model.addAttribute("appointmentId", null);
        return "admin/appointments/form";
    }

    @PostMapping("/create")
    public String create(
        @Valid @ModelAttribute("appointmentForm") AdminAppointmentForm form,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateOptions(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("appointmentId", null);
            return "admin/appointments/form";
        }
        appointmentService.createByAdmin(form);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo lịch khám thành công");
        return "redirect:/admin/appointments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("appointmentForm", appointmentService.buildAdminForm(appointmentService.getAppointmentById(id)));
        populateOptions(model);
        model.addAttribute("isEdit", true);
        model.addAttribute("appointmentId", id);
        return "admin/appointments/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("appointmentForm") AdminAppointmentForm form,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateOptions(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("appointmentId", id);
            return "admin/appointments/form";
        }
        appointmentService.updateByAdmin(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Cap nhat lich kham thanh cong");
        return "redirect:/admin/appointments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteByAdmin(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xoa lich kham thanh cong");
        return "redirect:/admin/appointments";
    }

    private void populateOptions(Model model) {
        model.addAttribute("patients", appointmentService.getAllPatients());
        model.addAttribute("doctors", appointmentService.getAllDoctors());
        model.addAttribute("statuses", AppointmentStatus.values());
    }
}
