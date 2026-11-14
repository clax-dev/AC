package com.example.cliente.entity;

import jakarta.persistence.*;

@Entity
@Table(name="empresa")
public class Empresa {
    // constructor vacío para hibernate
    protected Empresa() {}
    // constructor para instanciar el objeto SIN ID, asignado automáticamente por la tabla
    public Empresa(Cliente cliente, String razon_social, String cif, String email, String telefono, String sector) {
        this.cliente = cliente;
        this.razon_social = razon_social;
        this.cif = cif;
        this.email = email;
        this.telefono = telefono;
        this.sector = sector;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) // se genera solo
    private Integer id;
    @OneToOne() // 1:1
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private String razon_social;
    private String cif;
    private String email;
    private String telefono;
    private String sector;
}
