package com.pizzaria.service;

public class PedidoNaoEncontradoException extends RuntimeException {

    public PedidoNaoEncontradoException(String id) {
        super("Pedido não encontrado com id: " + id);
    }
}
