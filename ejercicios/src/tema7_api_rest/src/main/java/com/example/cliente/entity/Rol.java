package com.example.cliente.entity;


import jakarta.persistence.*;

@Entity
@Table(name="rol")
public class Rol {
    // constructor para hibernate
    protected Rol() {

    }

    // constructor para crear los objetos Rol
    public Rol (long id, TipoRol tipoRol) {
        this.id = id;
        this.tipoRol = tipoRol;
    }

    @Id
    private long id;
    @Enumerated(EnumType.STRING) // enum guardado y leido como texto
    @Column(name="tipo_rol")
    private TipoRol tipoRol;


    public void setTipoRol(TipoRol tipoRol) {
        this.tipoRol = tipoRol;
    }

    public long getId() {
        return id;
    }

    public TipoRol getTipoRol() {
        return tipoRol;
    }
}
