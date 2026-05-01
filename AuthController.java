package com.sidas.controller;

import com.sidas.entity.Ticket;
import com.sidas.entity.User;
import com.sidas.repository.TicketRepository;
import com.sidas.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TicketRepository ticketRepository;

    public AuthController(UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default role
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        List<Ticket> tickets = ticketRepository.findByUser(user);

        long total = ticketRepository.countByUser(user);
        long open = ticketRepository.countByUserAndStatus(user, "OPEN");
        long progress = ticketRepository.countByUserAndStatus(user, "IN_PROGRESS");
        long resolved = ticketRepository.countByUserAndStatus(user, "RESOLVED");

        model.addAttribute("tickets", tickets);
        model.addAttribute("totalTickets", total);
        model.addAttribute("openTickets", open);
        model.addAttribute("progressTickets", progress);
        model.addAttribute("resolvedTickets", resolved);

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
    
}