package com.example.cliente.service;

import com.example.cliente.dto.ClientePedidoDTO;
import com.example.cliente.dto.ClienteRequestDTO;
import com.example.cliente.dto.ClienteResponseDTO;
import com.example.cliente.dto.PedidoDTO;
import com.example.cliente.entity.Cliente;
import com.example.cliente.entity.Pedido;
import com.example.cliente.entity.PermisoCliente;
import com.example.cliente.entity.TipoCliente;
import com.example.cliente.mapper.ClienteMapper;
import com.example.cliente.mapper.PedidoMapper;
import com.example.cliente.repositories.ClienteRepository;
import com.example.cliente.repositories.PedidoRepository;
import com.example.cliente.repositories.PermisosClienteRepository;
import com.example.cliente.security.UsuarioActual;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final PermisosClienteRepository permisosClienteRepository;
    private final UsuarioActual usuarioActual;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper, PedidoRepository pedidoRepository, PedidoMapper pedidoMapper, PermisosClienteRepository permisosClienteRepository, UsuarioActual usuarioActual) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.permisosClienteRepository = permisosClienteRepository;
        this.usuarioActual = usuarioActual;
    }

    /** Método que realiza un select en la tabla de CLIENT_PERMISSION
     * para ver que clientes tiene asignado un usuario
     * SOLO SI NO ERES ADMIN
     * @param login usuario al que vemos sus clientes asignados
     * @return usuarios asignados
     */
    private List<ClienteResponseDTO> clientesDelUsuario(String login) {
        return permisosClienteRepository.findByUsuarioUsuarioLogin(login)
                .stream()
                .map(PermisoCliente::getCliente) // saca cada cliente
                .map(clienteMapper::toDto)
                .toList();
    }


    /**
     * Funcion que comprueba tu rol (ADMIN O USER)
     * Si eres admin --> todos los clientes
     * Si no --> clientesDelUsuario(user) --> muestra tus clientes
     * @return | Si eres admin --> todos los clientes
     *         | Si no --> clientesDelUsuario(user) --> muestra tus clientes
     */
    public List<ClienteResponseDTO> listarClientes() {
        // mostramos todos los clientes al usuario ADMIN
        if (usuarioActual.esAdmin() ) {
            return clienteRepository.findAll()                //    sí → todos
                    .stream()
                    .map(clienteMapper::toDto)
                    .toList();
        } else {
            // cogemos el usuario que no es admin
            return clientesDelUsuario(usuarioActual.login());
        }
    }

    // MÉTODOS PARA LISTAR LOS CLIENTES DE CADA USUARIO
    // ===================================================================================================================================================
    /**
     * Comprueba si realmente tiene ese cliente o no asignado
     * Si no es admin y no tiene el cliente asignado devolvemos 404 (para no desvelar si realmente existe o no el cliente que esta tratando de buscar)
     * De tenerlo asignado devolvemos cliente
     * @param id del cliente
     * @return cliente asignado o 404 ese cliente no "existe" para ti porque no es tuyo
     */
    public List<PedidoDTO> pedidosPorId(long id) {
        if (!usuarioActual.esAdmin() && !permisosClienteRepository.existsByUsuarioUsuarioLoginAndClienteId(usuarioActual.login(), id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return pedidoRepository.findByClienteId(id)
                .stream()
                .map(pedidoMapper::toDto)
                .toList();
    }

    /**
     * Comprueba si realmente tiene ese cliente o no asignado, buscando por su codigo
     * Si no es admin y no tiene el cliente asignado devolvemos 404 (para no desvelar si realmente existe o no el cliente que esta tratando de buscar)
     * De tenerlo asignado devolvemos cliente
     * @param codigo del cliente
     * @return cliente asignado o 404 ese cliente no "existe" para ti porque no es tuyo
     */
    public ClienteResponseDTO buscarPorCodigo(String codigo) {
        if (!usuarioActual.esAdmin() && !permisosClienteRepository.existsByUsuarioUsuarioLoginAndClienteCodigo(usuarioActual.login(), codigo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return clienteRepository.findByCodigo(codigo)
                .map(clienteMapper::toDto)
                .orElse(null);
    }

    /**
     * Muestra los clientes activos segun el rol del usuario
     * Si es admin devolvemos todos los clientes activos
     * Si no es admin buscamos sus permisos y devolvemos solo los clientes activos que tiene asignados
     * Si no tiene ninguno devolvemos la lista vacia (no es un error, simplemente no hay ninguno para ti)
     * @return lista de clientes activos asignados (o todos si es admin)
     */
    public List<ClienteResponseDTO> listarActivos() {
        if (usuarioActual.esAdmin()) {
            return clienteRepository.findByActivoTrueOrderByCodigoAsc()
                    .stream()
                    .map(clienteMapper::toDto)
                    .toList();
        }
        return permisosClienteRepository
                .findByUsuarioUsuarioLoginAndClienteActivoTrueOrderByClienteCodigoAsc(usuarioActual.login()) //muestra solo los tuyos
                .stream()
                .map(PermisoCliente::getCliente)
                .map(clienteMapper::toDto)
                .toList();
    }

    /**
     * Muestra los clientes de tipo empresa segun el rol del usuario
     * Si es admin devolvemos todas las empresas
     * Si no es admin buscamos sus permisos y devolvemos solo las empresas que tiene asignadas
     * Si no tiene ninguna devolvemos la lista vacia (no es un error, simplemente no hay ninguna para ti)
     * @return lista de empresas asignadas (o todas si es admin)
     */
    public List<ClienteResponseDTO> buscarClienteEmpresa() {
        if (usuarioActual.esAdmin()) {
            return clienteRepository.findByTipo(TipoCliente.EMPRESA)
                    .stream()
                    .map(clienteMapper::toDto)
                    .toList();
        }
        return permisosClienteRepository
                .findByUsuarioUsuarioLoginAndClienteTipo(usuarioActual.login(), TipoCliente.EMPRESA) //muestra los tuyos solo
                .stream()
                .map(PermisoCliente::getCliente)
                .map(clienteMapper::toDto)
                .toList();
    }
    // ===================================================================================================================================================

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

