package com.pizzaria.controller;

import com.pizzaria.entity.Perfil;
import com.pizzaria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("perfis", Perfil.values());
        return "admin/usuarios";
    }

    @PostMapping("/{id}/perfil")
    public String alterarPerfil(@PathVariable String id, @RequestParam Perfil perfil) {
        usuarioService.alterarPerfil(id, perfil);
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id) {
        usuarioService.excluir(id);
        return "redirect:/admin/usuarios";
    }
}
