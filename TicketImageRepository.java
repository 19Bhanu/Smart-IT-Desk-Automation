package com.sidas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidas.entity.TicketImage;

public interface TicketImageRepository extends JpaRepository<TicketImage, Long> {

}