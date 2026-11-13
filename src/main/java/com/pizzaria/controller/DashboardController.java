package com.pizzaria.controller;

import com.pizzaria.entity.Pedido;
import com.pizzaria.service.DashboardService;
import com.pizzaria.service.PedidoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/indicadores")
    public Map<String, Object> indicadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sabor,
            @RequestParam(required = false) String atendente) {
        return dashboardService.indicadores(filtrar(inicio, fim, status, sabor, atendente));
    }

    @GetMapping("/status")
    public Map<String, Long> porStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sabor,
            @RequestParam(required = false) String atendente) {
        return dashboardService.porStatus(filtrar(inicio, fim, status, sabor, atendente));
    }

    @GetMapping("/sabores")
    public Map<String, Long> rankingSabores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sabor,
            @RequestParam(required = false) String atendente) {
        return dashboardService.rankingSabores(filtrar(inicio, fim, status, sabor, atendente));
    }

    @GetMapping("/faturamento-por-periodo")
    public Map<String, Double> faturamentoPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sabor,
            @RequestParam(required = false) String atendente) {
        return dashboardService.faturamentoPorPeriodo(filtrar(inicio, fim, status, sabor, atendente));
    }

    private List<Pedido> filtrar(LocalDate inicio, LocalDate fim, String status, String sabor, String atendente) {
        return pedidoService.listarPedidos(inicio, fim, status, sabor, atendente);
    }
}
