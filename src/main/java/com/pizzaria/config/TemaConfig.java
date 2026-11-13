package com.pizzaria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class TemaConfig {

    @Value("${app.nome}")
    private String nomeSistema;

    @Value("${app.tema}")
    private String tema;

    @ModelAttribute("nomeSistema")
    public String nomeSistema() {
        return nomeSistema;
    }

    @ModelAttribute("tema")
    public String tema() {
        return tema;
    }
}
