package com.sidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidas.entity.Ticket;
import com.sidas.entity.User;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser(User user);

    long countByUser(User user);

    long countByUserAndStatus(User user, String status);
    List<Ticket> findByStatus(String status);
}