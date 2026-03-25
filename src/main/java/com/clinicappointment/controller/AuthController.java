package com.clinicappointment.controller;

import com.clinicappointment.dto.RegisterForm;
import com.clinicappointment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
        @Valid @ModelAttribute("registerForm") RegisterForm registerForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.registerPatient(registerForm);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registerError", ex.getMessage());
            return "auth/register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Dang ky thanh cong, vui long dang nhap");
        return "redirect:/login";
    }
}
