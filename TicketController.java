package com.sidas.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sidas.entity.*;
import com.sidas.repository.*;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final TicketImageRepository imageRepository;
    private final UserRepository userRepository;

    public TicketController(TicketRepository ticketRepository,
                            TicketImageRepository imageRepository,
                            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public String showCreatePage() {
        return "create-ticket";
    }

    @PostMapping("/create")
    public String createTicket(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority,
            @RequestParam("images") MultipartFile[] images,
            Authentication authentication
    ) throws IOException {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        Ticket ticket = new Ticket();

        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUser(user);

        ticketRepository.save(ticket);

        List<TicketImage> imageList = new ArrayList<>();

        for (MultipartFile file : images) {

            if (!file.isEmpty()) {

                String uploadDir = System.getProperty("user.dir") + "/uploads/";

                File uploadFolder = new File(uploadDir);

                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                File dest = new File(uploadDir + fileName);

                file.transferTo(dest);

                TicketImage img = new TicketImage();
                img.setImagePath(fileName);
                img.setTicket(ticket);

                imageRepository.save(img);

                imageList.add(img);
            }
        }

        ticket.setImages(imageList);

        return "redirect:/dashboard";
    }
    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        model.addAttribute("ticket", ticket);

        return "ticket-details";
    }

    @GetMapping
    public String myTickets(Model model, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        List<Ticket> tickets = ticketRepository.findByUser(user);

        model.addAttribute("tickets", tickets);

        return "dashboard";
    }
}