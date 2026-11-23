package com.example.cliente.repositories;

import com.example.cliente.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

// guarda y consulta empresas; save() lo da JpaRepository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
}
