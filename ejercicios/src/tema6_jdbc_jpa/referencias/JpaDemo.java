package tema6_jdbc_jpa.referencias;

import jakarta.persistence.*;
import tema6_jdbc_jpa.clasesJpa.Cliente;
import tema6_jdbc_jpa.clasesJpa.Pedido;
import tema6_jdbc_jpa.clasesJpa.TipoCliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

//Misma idea que JdbcDemo, usando entidades y transacciones JPA.
public class JpaDemo {
    public static void main(String[] args) {
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("basededatosMySQL");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("basededatosPSQL");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            Cliente cliente = new Cliente("CLI-JPA-Ejemplo", TipoCliente.PERSONA, LocalDate.now());
            em.persist(cliente);
            em.persist(new Pedido(LocalDate.now(), new BigDecimal("64.90"), cliente));
            tx.commit();

            List<Pedido> pedidos = em.createQuery(
                            "SELECT p FROM Pedido p JOIN FETCH p.cliente ORDER BY p.fecha DESC", Pedido.class)
                    .getResultList();
            pedidos.forEach(p -> System.out.println(p.getId() + " | " + p.getCliente().getCodigo() + " | " + p.getTotal()));
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); emf.close(); }
    }

}

