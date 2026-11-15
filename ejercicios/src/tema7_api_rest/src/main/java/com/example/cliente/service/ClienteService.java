package com.example.cliente.service;

import com.example.cliente.dto.ClientePedidoDTO;
import com.example.cliente.dto.ClienteRequestDTO;
import com.example.cliente.dto.ClienteResponseDTO;
import com.example.cliente.dto.PedidoDTO;
import com.example.cliente.entity.Cliente;
import com.example.cliente.entity.Pedido;
import com.example.cliente.entity.TipoCliente;
import com.example.cliente.mapper.ClienteMapper;
import com.example.cliente.mapper.PedidoMapper;
import com.example.cliente.repositories.ClienteRepository;
import com.example.cliente.repositories.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
/** Contiene la lógica de la gestión de clientes.
 * Pide los datos a ClienteRepository y usa ClienteMapper para convertirlos a DTO,
 * devuelve DTO al Controller en forma de objeto temporal sin enviar directamente el objeto Cliente.
 */
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper, PedidoRepository pedidoRepository, PedidoMapper pedidoMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    // GET MAPPINGS
    // muestra los datos
    public List<ClienteResponseDTO> listarClientes() {
        return clienteRepository.findAll() // busca todos
                .stream() // recorre los elementos
                .map(clienteMapper::toDto) // convierte cada elemento a DTO
                .toList(); // convierte a lista
    }

    // muestra los activos
    public List<ClienteResponseDTO> listarActivos() {
        return clienteRepository.findByActivoTrueOrderByCodigoAsc() // trae el cliente
                .stream() // recorre los elementos
                .map(clienteMapper::toDto) // convierte cada elemento a DTO
                .toList(); // convierte a lista
    }

    // buscar cliente por codigo
    public ClienteResponseDTO buscarPorCodigo(String codigo) {
        return clienteRepository.findByCodigo(codigo)
                .map(clienteMapper::toDto)
                .orElse(null);
    }

    // busca los clientes tipo empresa
    public List<ClienteResponseDTO> buscarClienteEmpresa() {
        return clienteRepository.findByTipo(TipoCliente.EMPRESA)
                .stream() // recorre los elementos
                .map(clienteMapper::toDto)
                .toList(); // convierte a lista;
    }

    // devuelve los pedidos buscados por ID
    public List<PedidoDTO> pedidosPorId(long id) {
        return pedidoRepository.findByClienteId(id) // busca el ID cliente
                .stream()  // recorre elementos
                .map(pedidoMapper::toDto) // convierte cada pedido a DTO
                .toList(); // convierte a lista
    }

    // POST MAPPINGS
    // crea cliente
    public ClienteResponseDTO registrarCliente(ClienteRequestDTO dto) {
        Cliente clienteMap = clienteMapper.toEntity(dto);
        Cliente cliente = clienteRepository.save(clienteMap);
        return clienteMapper.toDto(cliente);
    }

    //  crear pedido a cliente existente
    public PedidoDTO crearPedido(PedidoDTO dto, Long clienteId) {
        // busqueda del cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // creacion del nuevo pedido
        Pedido pedidoMap = pedidoMapper.toEntity(dto, cliente);
        Pedido pedido = pedidoRepository.save(pedidoMap);
        return pedidoMapper.toDto(pedido);
    }

    // recibe un cliente y un pedido juntos si cliente no existe lo crea, seguidamente asocia el pedido
    // recibe un cliente y un pedido juntos: si el cliente no existe lo crea y luego asocia el pedido
    public PedidoDTO crearClienteConPedido(ClientePedidoDTO dto) {
        String codigo = dto.cliente().codigo();

        // comprobar si existe el cliente
        if (clienteRepository.existsByCodigo(codigo)) {
            // si existe creamos el pedido para ese cliente
            Cliente clienteViejo = clienteRepository.findByCodigo(codigo).get();
            return crearPedido(dto.pedido(), clienteViejo.getId());
        } else {
            // si no existe creamos el cliente y luego su pedido y lo asignamos
            ClienteResponseDTO clienteNuevo = registrarCliente(dto.cliente());
            return crearPedido(dto.pedido(), clienteNuevo.id());
        }
    }
}

