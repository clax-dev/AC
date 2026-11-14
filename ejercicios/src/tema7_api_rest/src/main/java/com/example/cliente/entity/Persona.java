package com.example.cliente.entity;

import jakarta.persistence.*;

@Entity
@Table(name="persona")
public class Persona {
    // constructor vacio utilizado pr hibernate
    protected Persona() {}

    // constructor que utilizamos para la creacion de objetos
    public Persona(Cliente cliente, String nombre, String apellidos, String dni, String email, String telefono) {
        this.cliente = cliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
    }

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY) // indica que se genera solo.
    private Long id;
    // Foreign key cliente_id
    @OneToOne() // 1:1
    @JoinColumn(name="cliente_id")
    private Cliente cliente;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;

    // El campo id no tiene SETTER porque se genera solo
    //GETTERS AND SETTERS
    public Cliente getCliente() {
        return cliente;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
