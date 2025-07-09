package com.agnese.weatherapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller per gestire il reindirizzamento della root URL.
 * Questo controller reindirizza le richieste dalla root ("/") a "/index.html".
 * Utilizza un reindirizzamento lato client (HTTP 302) per evitare loop di forwarding lato server.
 */
@Controller
public class RedirectController {

    /**
     * Mappa le richieste HTTP GET alla root URL ("/") e reindirizza a "/index.html".
     * Quando un utente visita http://localhost:8080, il browser verrà istruito
     * a fare una nuova richiesta a http://localhost:8080/index.html.
     *
     * @return Un oggetto RedirectView che specifica l'URL di reindirizzamento.
     */
    @GetMapping("/")
    public RedirectView redirectToIndex() {
        // Crea un RedirectView che punta a "/index.html".
        // Questo causerà un reindirizzamento HTTP 302 (Found) nel browser.
        return new RedirectView("/index.html");
    }
}

