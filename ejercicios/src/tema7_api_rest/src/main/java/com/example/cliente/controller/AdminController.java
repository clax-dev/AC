package com.example.cliente.controller;

import com.example.cliente.dto.AdminResumeDTO;
import com.example.cliente.service.AdminService;
import com.example.cliente.lector.MigracionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin") // ruta del admin
public class AdminController {
    private final AdminService AdminService;
    private final MigracionService migracionService;

    public AdminController(AdminService adminService, MigracionService migracionService) {
        this.AdminService = adminService;
        this.migracionService = migracionService;
    }

    @GetMapping("/dashboard") //si se pide esa ruta coge el metodo
    public AdminResumeDTO datosAdmin() {
        return AdminService.resumen();
    }

    // lanza la migracion de los ficheros CSV a la base de datos (solo ADMIN, por /api/admin/**)
    @PostMapping("/migracion")
    public String migrar() {
        return migracionService.migrar();
    }
}
