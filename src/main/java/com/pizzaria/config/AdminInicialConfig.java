package com.pizzaria.config;

import com.pizzaria.entity.Perfil;
import com.pizzaria.entity.Usuario;
import com.pizzaria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInicialConfig implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.senha}")
    private String senha;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioService.emailJaCadastrado(email)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail(email);
        admin.setSenha(senha);
        usuarioService.salvarNovo(admin, Perfil.ADMIN);
    }
}
