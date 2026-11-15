package com.example.cliente.repositories;

import com.example.cliente.entity.Cliente;
import com.example.cliente.entity.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // coge los clientes de la base de datos
    Optional<Cliente> findByCodigo(String codigo);
    // hace un select por activo y lo ordena por código
    List<Cliente> findByActivoTrueOrderByCodigoAsc();
    // hace un select por tipo
    List<Cliente> findByTipo(TipoCliente tipo);

    boolean existsByCodigo(String codigo);
}
