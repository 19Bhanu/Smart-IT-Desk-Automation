package com.sidas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sidas.entity.Ticket;
import com.sidas.repository.TicketRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            Model model) {

        List<Ticket> tickets;

        // FILTER
        if (status != null && !status.isEmpty()) {
            tickets = ticketRepository.findByStatus(status);
        } else {
            tickets = ticketRepository.findAll();
        }

        // SORT
        if ("oldest".equals(sort)) {
            tickets.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        } else {
            tickets.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSort", sort);

        return "admin-dashboard";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        ticket.setStatus(status);

        ticketRepository.save(ticket);

        return "redirect:/admin/dashboard";
    }
    @GetMapping("/ticket/{id}")
        public String viewTicketAdmin(@PathVariable Long id, Model model) {

            Ticket ticket = ticketRepository.findById(id).orElseThrow();

            model.addAttribute("ticket", ticket);

            return "admin-ticket-details";
        }
        @PostMapping("/add-comment/{id}")
        public String addComment(@PathVariable Long id,
                                @RequestParam String comment) {

            Ticket ticket = ticketRepository.findById(id).orElseThrow();

            ticket.setAdminComment(comment);

            ticketRepository.save(ticket);

            return "redirect:/admin/ticket/" + id;
        }
}