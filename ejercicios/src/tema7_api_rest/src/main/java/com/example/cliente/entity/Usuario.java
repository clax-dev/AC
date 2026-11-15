package com.example.cliente.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// atributo tiene que ser un objeto ROL

@Entity
@Table(name="usuario")
public class Usuario {
    // constructor para el hibernate
    protected Usuario() {

    }

    //constructor para crear los objetos (id asignado por la BD automaticamente)
    public Usuario(Rol rol, String usuarioLogin, String contrasena) {
        this.rol = rol;
        this.usuarioLogin = usuarioLogin;
        this.contrasena = contrasena;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica que se genera solo.
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER) // cargamos relacion y entidad al mismo tiempo
    @JoinColumn(name="id_rol")
    private Rol rol; //sera un objeto rol
    @Column(name="usuario_login")
    private String usuarioLogin;
    private String contrasena;


    public Long getId() {
        return id;
    }

    public Rol getRol() {
        return rol;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
