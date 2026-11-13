package com.pizzaria.controller;

import com.pizzaria.entity.Usuario;
import com.pizzaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String formularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult resultado) {
        if (!resultado.hasFieldErrors("email") && usuarioService.emailJaCadastrado(usuario.getEmail())) {
            resultado.rejectValue("email", "email.duplicado", "E-mail já cadastrado");
        }
        if (resultado.hasErrors()) {
            return "cadastro";
        }
        usuarioService.cadastrar(usuario);
        return "redirect:/login?cadastro";
    }
}
