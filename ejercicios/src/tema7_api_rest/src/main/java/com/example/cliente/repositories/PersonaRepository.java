package com.example.cliente.repositories;

import com.example.cliente.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

// guarda y consulta personas; save() lo da JpaRepository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
