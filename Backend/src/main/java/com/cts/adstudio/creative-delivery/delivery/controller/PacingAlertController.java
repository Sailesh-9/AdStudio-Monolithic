package com.cts.delivery.controller;

import com.cts.delivery.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacing-alerts")
@RequiredArgsConstructor
public class PacingAlertController {

    private final AlertRepository repo;

    @GetMapping
    public Object getAll() {
        return repo.findAll();
    }
}