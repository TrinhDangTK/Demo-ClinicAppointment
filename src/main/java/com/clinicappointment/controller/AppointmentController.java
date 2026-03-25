package com.clinicappointment.controller;

import com.clinicappointment.dto.AppointmentCreateForm;
import com.clinicappointment.service.AppointmentService;
import com.clinicappointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enroll")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    public AppointmentController(AppointmentService appointmentService, DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String enrollHome(Authentication authentication, Model model) {
        model.addAttribute("appointments", appointmentService.getMyAppointments(authentication.getName()));
        return "enroll/index";
    }

    @GetMapping("/appointments/create/{doctorId}")
    public String createForm(@PathVariable Long doctorId, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(doctorId));
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("appointmentForm", new AppointmentCreateForm());
        return "enroll/appointment-form";
    }

    @PostMapping("/appointments/create/{doctorId}")
    public String create(
        @PathVariable Long doctorId,
        @Valid @ModelAttribute("appointmentForm") AppointmentCreateForm form,
        BindingResult bindingResult,
        Authentication authentication,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", doctorService.getDoctorById(doctorId));
            model.addAttribute("doctorId", doctorId);
            return "enroll/appointment-form";
        }

        try {
            appointmentService.createAppointment(authentication.getName(), doctorId, form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("doctor", doctorService.getDoctorById(doctorId));
            model.addAttribute("doctorId", doctorId);
            model.addAttribute("bookingError", ex.getMessage());
            return "enroll/appointment-form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Dat lich kham thanh cong");
        return "redirect:/enroll";
    }

    @GetMapping("/appointments/{id}")
    public String appointmentDetail(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("appointment", appointmentService.getMyAppointmentForPatient(authentication.getName(), id));
        return "enroll/appointment-detail";
    }

    @GetMapping("/my-appointments")
    public String myAppointments(Authentication authentication, Model model) {
        model.addAttribute("appointments", appointmentService.getMyAppointments(authentication.getName()));
        return "enroll/my-appointments";
    }

    @PostMapping("/my-appointments/{id}/cancel")
    public String cancelAppointment(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        appointmentService.cancelMyAppointment(authentication.getName(), id);
        redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch khám thành công!");
        return "redirect:/enroll";
    }
}
