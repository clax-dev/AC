package com.example.cliente.entity;

import jakarta.persistence.*;

@Entity
@Table(name="client_permission")
public class PermisoCliente {
    // constructor para hibernate
    protected PermisoCliente() {
    }

    // constructor para nosotros crear los objetos
    public PermisoCliente(long id, Usuario usuario, Cliente cliente, TipoPermiso permiso) {
        this.id = id;
        this.usuario = usuario;
        this.cliente = cliente;
        this.permiso = permiso;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne() // cargamos relacion y entidad al mismo tiempo
    @JoinColumn(name="usuario", referencedColumnName="usuario_login")
    private Usuario usuario;
    @ManyToOne()
    @JoinColumn(name="codigo_cliente", referencedColumnName="codigo")
    private Cliente cliente;
    @Enumerated(EnumType.STRING)
    private TipoPermiso permiso;

    public Usuario getUsuario() {
        return usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public TipoPermiso getPermiso() {
        return permiso;
    }

    public long getId() {
        return id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setPermiso(TipoPermiso permiso) {
        this.permiso = permiso;
    }
}
