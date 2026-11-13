package com.pizzaria.service;

import com.pizzaria.entity.Pedido;
import com.pizzaria.repository.PedidoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    public static final String ENTREGUE = "Entregue";

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido criarPedido(Pedido pedido) {
        pedido.setId(null);
        return pedidoRepository.save(tratarDados(pedido));
    }

    public Pedido atualizarPedido(String id, Pedido pedidoAtualizado) {
        Pedido pedido = buscarPorId(id);
        pedido.setCliente(pedidoAtualizado.getCliente());
        pedido.setDescricao(pedidoAtualizado.getDescricao());
        pedido.setSabor(pedidoAtualizado.getSabor());
        pedido.setStatus(pedidoAtualizado.getStatus());
        pedido.setValor(pedidoAtualizado.getValor());
        pedido.setAtendente(pedidoAtualizado.getAtendente());
        pedido.setObservacao(pedidoAtualizado.getObservacao());
        pedido.setDataEntrega(pedidoAtualizado.getDataEntrega());
        return pedidoRepository.save(tratarDados(pedido));
    }

    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }

    public List<Pedido> listarPedidos(LocalDate inicio, LocalDate fim, String status, String sabor, String atendente) {
        return pedidoRepository.findAll().stream()
                .filter(p -> inicio == null || (p.getDataPedido() != null && !p.getDataPedido().isBefore(inicio)))
                .filter(p -> fim == null || (p.getDataPedido() != null && !p.getDataPedido().isAfter(fim)))
                .filter(p -> vazio(status) || status.equalsIgnoreCase(p.getStatus()))
                .filter(p -> vazio(sabor) || sabor.equalsIgnoreCase(p.getSabor()))
                .filter(p -> vazio(atendente) || atendente.equalsIgnoreCase(p.getAtendente()))
                .toList();
    }

    private Pedido tratarDados(Pedido pedido) {
        pedido.setCliente(pedido.getCliente().trim());
        pedido.setDescricao(pedido.getDescricao().trim());
        pedido.setSabor(pedido.getSabor().trim());
        pedido.setStatus(pedido.getStatus().trim());
        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDate.now());
        }
        if (ENTREGUE.equalsIgnoreCase(pedido.getStatus()) && pedido.getDataEntrega() == null) {
            pedido.setDataEntrega(LocalDate.now());
        }
        if (!ENTREGUE.equalsIgnoreCase(pedido.getStatus())) {
            pedido.setDataEntrega(null);
        }
        return pedido;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
