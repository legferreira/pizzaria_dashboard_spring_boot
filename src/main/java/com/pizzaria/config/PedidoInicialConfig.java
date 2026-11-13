package com.pizzaria.config;

import com.pizzaria.entity.Pedido;
import com.pizzaria.repository.PedidoRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoInicialConfig implements CommandLineRunner {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (pedidoRepository.count() > 0) {
            return;
        }
        LocalDate hoje = LocalDate.now();
        pedidoRepository.save(novoPedido("Ana", "Pizza grande", "Calabresa", "Entregue", 59.90, "Bruno",
                "Sem cebola", hoje.minusDays(2), hoje.minusDays(2)));
        pedidoRepository.save(novoPedido("Rafael", "Pizza média", "Mussarela", "Em preparo", 45.00, "Paula",
                null, hoje, null));
    }

    private Pedido novoPedido(String cliente, String descricao, String sabor, String status, Double valor,
            String atendente, String observacao, LocalDate dataPedido, LocalDate dataEntrega) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDescricao(descricao);
        pedido.setSabor(sabor);
        pedido.setStatus(status);
        pedido.setValor(valor);
        pedido.setAtendente(atendente);
        pedido.setObservacao(observacao);
        pedido.setDataPedido(dataPedido);
        pedido.setDataEntrega(dataEntrega);
        return pedido;
    }
}
