package com.example.cliente.lector;

import com.example.cliente.entity.Cliente;
import com.example.cliente.entity.Empresa;
import com.example.cliente.entity.Pedido;
import com.example.cliente.entity.Persona;
import com.example.cliente.entity.TipoCliente;
import com.example.cliente.repositories.ClienteRepository;
import com.example.cliente.repositories.EmpresaRepository;
import com.example.cliente.repositories.PedidoRepository;
import com.example.cliente.repositories.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Pasa los datos de los ficheros antiguos (CSV) a la base de datos
 * 1. lee los clientes y los guarda en cliente y en persona o empresa
 * 2. lee los pedidos y los une con su cliente por el codigo
 * 3. va todo en una transaccion, si falla una fila no se guarda nada
 */
@Service
@Transactional
public class MigracionService {

    // rutas de los ficheros, relativas a la carpeta del proyecto
    private static final Path FICHERO_CLIENTES = Path.of("datos", "clientes.csv");
    private static final Path FICHERO_PEDIDOS = Path.of("datos", "pedidos.csv");

    // posiciones de las columnas del fichero de pedidos
    private static final int PEDIDO_CODIGO_CLIENTE = 1;
    private static final int PEDIDO_FECHA = 2;
    private static final int PEDIDO_TOTAL = 3;

    private final LectorCsv lector;
    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final EmpresaRepository empresaRepository;
    private final PedidoRepository pedidoRepository;

    public MigracionService(LectorCsv lector, ClienteRepository clienteRepository,
                            PersonaRepository personaRepository, EmpresaRepository empresaRepository,
                            PedidoRepository pedidoRepository) {
        this.lector = lector;
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
        this.empresaRepository = empresaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * metodo que lanza la migracion, primero clientes y luego pedidos
     * (si no existe el cliente no se puede guardar su pedido)
     * @return mensaje con cuantos clientes y pedidos se han pasado
     */
    public String migrar() {
        int clientes = migrarClientes();
        int pedidos = migrarPedidos();
        return "Migracion completada: " + clientes + " clientes y " + pedidos + " pedidos";
    }

    /**
     * Recorre las filas de clientes.csv
     * 1. saca los datos comunes con el enum MoldeCliente y los pasa a su tipo
     * 2. guarda el cliente (vuelve con el id de la bd)
     * 3. si es PERSONA lo guarda en persona, si no en empresa
     * @return numero de clientes guardados
     */
    private int migrarClientes() {
        List<String[]> filas = lector.leer(FICHERO_CLIENTES);

        for (String[] fila : filas) {
            // columnas comunes (tabla cliente)
            TipoCliente tipo = TipoCliente.valueOf(columna(fila, MoldeCliente.TIPO_REGISTRO));
            String codigo = columna(fila, MoldeCliente.CODIGO);
            LocalDate fechaAlta = LocalDate.parse(columna(fila, MoldeCliente.FECHA_ALTA));
            boolean activo = Boolean.parseBoolean(columna(fila, MoldeCliente.ACTIVO));

            Cliente cliente = new Cliente(codigo, tipo, fechaAlta);
            cliente.setActivo(activo); // el constructor lo pone siempre a true
            cliente = clienteRepository.save(cliente); // ya viene con el id de la bd

            // datos especificos segun el tipo
            if (tipo == TipoCliente.PERSONA) {
                personaRepository.save(new Persona(cliente,
                        columna(fila, MoldeCliente.NOMBRE),
                        columna(fila, MoldeCliente.APELLIDOS),
                        columna(fila, MoldeCliente.DNI),
                        columna(fila, MoldeCliente.EMAIL),
                        columna(fila, MoldeCliente.TELEFONO)));
            } else {
                empresaRepository.save(new Empresa(cliente,
                        columna(fila, MoldeCliente.RAZON_SOCIAL),
                        columna(fila, MoldeCliente.CIF),
                        columna(fila, MoldeCliente.EMAIL),
                        columna(fila, MoldeCliente.TELEFONO),
                        columna(fila, MoldeCliente.SECTOR)));
            }
        }
        return filas.size();
    }

    /**
     * Recorre las filas de pedidos.csv
     * 1. busca el cliente por su codigo, si no existe da error
     * 2. pasa la fecha y el total a su tipo
     * 3. guarda el pedido unido a su cliente
     * @return numero de pedidos guardados
     */
    private int migrarPedidos() {
        List<String[]> filas = lector.leer(FICHERO_PEDIDOS);

        for (String[] fila : filas) {
            String codigoCliente = fila[PEDIDO_CODIGO_CLIENTE].trim();
            Cliente cliente = clienteRepository.findByCodigo(codigoCliente)
                    .orElseThrow(() -> new IllegalStateException(
                            "Pedido con cliente inexistente: " + codigoCliente));

            LocalDate fecha = LocalDate.parse(fila[PEDIDO_FECHA].trim());
            BigDecimal total = new BigDecimal(fila[PEDIDO_TOTAL].trim());

            pedidoRepository.save(new Pedido(fecha, total, cliente));
        }
        return filas.size();
    }

    /**
     * coge el dato de una columna de la fila con el enum
     * si esta vacia devuelve null, asi en la bd queda NULL y no un texto vacio
     * @param fila fila del fichero separada en columnas
     * @param columna columna que queremos coger
     * @return el dato o null si no hay
     */
    private String columna(String[] fila, MoldeCliente columna) {
        String valor = fila[columna.getPosicion()].trim();
        return valor.isEmpty() ? null : valor;
    }
}
