package com.example.cliente.controller;

import com.example.cliente.dto.AdminResumeDTO;
import com.example.cliente.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin") // ruta del admin
public class AdminController {
    private final AdminService AdminService;

    public AdminController(AdminService adminService) {
        this.AdminService = adminService;
    }

    @GetMapping("/dashboard") //si se pide esa ruta coge el metodo
    public AdminResumeDTO datosAdmin() {
        return AdminService.resumen();
    }
}
