package tema6_jdbc_jpa.clasesJpa;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cliente")
public class Cliente {
    // constructor vacío para que hibernate cree objetos
    protected Cliente() {
    }

    // constructor para crear clientes nuevos (id aignado por la bd y activo true por defecto)
    public Cliente(String codigo, TipoCliente tipo, LocalDate fechaAlta) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.fechaAlta = fechaAlta;
        this.activo = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica que se genera solo.
    private Long id;

    private String codigo;
    @Enumerated(EnumType.STRING)
    private TipoCliente tipo;
    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;
    private boolean activo;

    // El campo id no tiene SETTER porque se genera solo
    // GETTERS AND SETTERS

    public Long getId() {
        return id;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public boolean isActivo() {
        return activo;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
