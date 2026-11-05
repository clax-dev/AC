package tema6_jdbc_jpa.clasesJpa;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="pedido")
public class Pedido {
    // Constructor vacio para q hibernate cree objetos
    protected Pedido() {}
    // Constructor que utilizamos para instanciar objetos, SIN ID -> ASIGNADO POR LA TABLA
    public Pedido(LocalDate fecha, BigDecimal total, Cliente cliente) {
        this.fecha=fecha;
        this.total=total;
        this.cliente=cliente;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) // se genera solo
    private Integer id;
    private LocalDate fecha;
    private BigDecimal total;
    // Foreign Key cliente
    @ManyToOne // N:1
    @JoinColumn(name="cliente_id")
    private Cliente cliente;

    // El campo id no tiene SETTER porque se genera solo
    // GETTERS AND SETTERS
    public Integer getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
