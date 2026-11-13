package com.pizzaria.service;

import com.pizzaria.entity.Pedido;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public Map<String, Object> indicadores(List<Pedido> pedidos) {
        List<Pedido> entregues = entregues(pedidos);
        double faturamento = entregues.stream().mapToDouble(Pedido::getValor).sum();
        double ticketMedio = entregues.isEmpty() ? 0 : faturamento / entregues.size();

        Map<String, Object> indicadores = new LinkedHashMap<>();
        indicadores.put("total", pedidos.size());
        indicadores.put("emPreparo", contarPorStatus(pedidos, "Recebido") + contarPorStatus(pedidos, "Em preparo"));
        indicadores.put("entregues", entregues.size());
        indicadores.put("cancelados", contarPorStatus(pedidos, "Cancelado"));
        indicadores.put("faturamento", arredondar(faturamento));
        indicadores.put("ticketMedio", arredondar(ticketMedio));
        return indicadores;
    }

    public Map<String, Long> porStatus(List<Pedido> pedidos) {
        return pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getStatus, TreeMap::new, Collectors.counting()));
    }

    public Map<String, Long> rankingSabores(List<Pedido> pedidos) {
        return pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getSabor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<String, Double> faturamentoPorPeriodo(List<Pedido> pedidos) {
        return entregues(pedidos).stream()
                .collect(Collectors.groupingBy(p -> p.getDataEntrega().toString(), TreeMap::new,
                        Collectors.summingDouble(Pedido::getValor)));
    }

    private List<Pedido> entregues(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(p -> PedidoService.ENTREGUE.equalsIgnoreCase(p.getStatus()) && p.getDataEntrega() != null)
                .toList();
    }

    private long contarPorStatus(List<Pedido> pedidos, String status) {
        return pedidos.stream().filter(p -> status.equalsIgnoreCase(p.getStatus())).count();
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
