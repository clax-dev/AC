package tema6_jdbc_jpa;

import jakarta.persistence.*;
import tema6_jdbc_jpa.clasesJpa.Cliente;
import tema6_jdbc_jpa.clasesJpa.Empresa;
import tema6_jdbc_jpa.clasesJpa.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class JpaRetos {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("basededatosPSQL");
        EntityManager em = emf.createEntityManager();
        Cliente c = em.find(Cliente.class, 1); // cliente que utilizamos
        LocalDate date = LocalDate.parse("2025-04-20");
        try (emf; em) {
            //RETO 1
            System.out.println("Método insertarPedido");
            insertarPedido(c.getId(), em, LocalDate.now(), new BigDecimal("50.00"));
            System.out.println("Método consultarLista");
            consultarLista(c, c.getId(), em);
            System.out.println("Método filtrarFechas");
            filtrarFechas(c, em, date);
            System.out.println("Método borrarCliente");
            borrarCliente(c, 4, em);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //RETO 1
    static void insertarPedido(long cliente_id, EntityManager em, LocalDate fecha, BigDecimal total) {
        // insertar un pedido para un cliente existente en la tabla pedido
        EntityTransaction tx = em.getTransaction();
        Cliente cliente = em.find(Cliente.class, cliente_id); // buscamos el cliente e instanciamos el objeto
        Pedido pedido = new Pedido(fecha, total, cliente); // creamos el pedido asignado al cliente que buscamos
        try {
            tx.begin(); // iniciamos acción y marcamos a donde vuelve en caso de rollback();
            em.persist(pedido); // insertamos pedido
            tx.commit(); // guardamos
            System.out.println("ID PEDIDO -> " + pedido.getId());
        } catch (Exception e) {
            System.out.println("No se pudo registrar un pedido, haciendo rollback...");
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    // RETO 2
    static void consultarLista(Cliente cliente, long cliente_id, EntityManager em) {
        // lista de pedidos del cliente introducido en el metodo (buscado a traves del id del cliente)
        List<Pedido> pedidos = em.createQuery("SELECT p FROM Pedido p WHERE p.cliente.id = :cliente_id", Pedido.class)
                .setParameter("cliente_id", cliente_id)
                .getResultList();
        if (pedidos.isEmpty()) {
            System.out.println("No se encontro el pedido");
        }
        for (Pedido p : pedidos) {
            //imprimimos
            System.out.println(p.getId() + " | " + p.getFecha() + " | " + p.getTotal() + " | " + p.getCliente().getCodigo());
        }


    }

    // RETO 3
    static void filtrarFechas(Cliente cliente, EntityManager em, LocalDate fechaLimite) {
        List<Pedido> fechaPedidos = em.createQuery("SELECT p FROM Pedido p WHERE p.fecha > :fecha", Pedido.class)
                .setParameter("fecha", fechaLimite)
                .getResultList();
        if (fechaPedidos.isEmpty()) {
            System.out.println("No hay fechas disponibles para ese pedido");
        }
        for (Pedido p : fechaPedidos) {
            System.out.println(p.getId() + " | " + p.getFecha() + " | " + p.getTotal()  + " | " + p.getCliente().getCodigo());
        }
    }

    // RETO 4
    static void borrarCliente(Cliente cliente, long cliente_id, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); // comenzamos
            // comprobación
            System.out.println("ANTES -> " + contarEmpresas(em, cliente_id));
            em.remove(em.find(Cliente.class, cliente_id)); // eliminamos cliente buscando por id
            tx.commit(); // guardamos
            em.clear();
            System.out.println("DESPUÉS -> " + contarEmpresas(em, cliente_id));
        } catch (Exception e) {
            System.out.println("No se encontró el pedido");
            if (tx.isActive()) {
                tx.rollback(); //volvemos si falla
            }
        }
    }

    static Long contarEmpresas(EntityManager em, long cliente_id) {
        return em.createQuery(
                        "SELECT COUNT(e) FROM Empresa e WHERE e.cliente.id = :id", Long.class)
                .setParameter("id", cliente_id)
                .getSingleResult();
    }

}
