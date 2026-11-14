package com.example.cliente.controller;

import com.example.cliente.dto.ClientePedidoDTO;
import com.example.cliente.dto.ClienteRequestDTO;
import com.example.cliente.dto.ClienteResponseDTO;
import com.example.cliente.dto.PedidoDTO;
import com.example.cliente.entity.Cliente;
import com.example.cliente.service.ClienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/clientes") // puerta de entrada (ruta) del API
/** Recibe las peticiones HTTP de los clientes de la API
 * envía el trabajo a ClienteService y devuelve un objeto DTO (copia temporal de Cliente).
 * NO accede directamente a la base de datos.
 */
public class ClienteController {
    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // atiende a la petición y llama a quien la realiza
    // GET MAPPING
    @GetMapping
    public List<ClienteResponseDTO> listarClientes() {
        // devuelve los clientes convertidos a dto
        return clienteService.listarClientes();
    }

    @GetMapping("/activos")
    public List<ClienteResponseDTO> listarActivos() {
        // devuelve todos los activos convertidos a dto
        return clienteService.listarActivos();
    }

    @GetMapping("/codigo/{codigo}")
    public ClienteResponseDTO buscarPorCodigo(@PathVariable String codigo) {
        // busca a un cliente por codigo, @PathVariable reemplaza {codigo}
        return clienteService.buscarPorCodigo(codigo);
    }

    @GetMapping("/empresa")
    public List<ClienteResponseDTO> buscarClienteEmpresa() {
        // busca los clientes tipo enmpresa
        return clienteService.buscarClienteEmpresa();
    }

    @GetMapping("/{id}/pedidos")
    public List<PedidoDTO> pedidosPorId(@PathVariable long id) {
        // devuelve los pedidos de un cliente por su ID
        return clienteService.pedidosPorId(id);
    }

    // POST MAPPING
    @PostMapping
    public ClienteResponseDTO registrarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) {
        // crea cliente
        return clienteService.registrarCliente(clienteRequestDTO);
    }

    @PostMapping("/{id}/pedidos")
    public PedidoDTO crearPedido(@RequestBody PedidoDTO pedidoDTO, @PathVariable long id) {
        // crear pedido y asociar a un cliente existente
        return clienteService.crearPedido(pedidoDTO, id);
    }

    @PostMapping("/con-pedido")
    public PedidoDTO crearClienteConPedido(@RequestBody ClientePedidoDTO dto) {
        return clienteService.crearClienteConPedido(dto);
    }


}
