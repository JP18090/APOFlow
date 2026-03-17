package com.apoflow.backend.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping(value = {
        "/",
        "/minhas-apos",
        "/nova-apo",
        "/pendencias",
        "/votacao",
        "/avaliacao-final",
        "/lancamento",
        "/notificacoes"
    })
    public String forward() {
        return "forward:/index.html";
    }
}