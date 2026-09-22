package aulab_chronicle.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ExceptionHandlingController {

    // Metodo dinamico che cattura gli errori HTTP (con focus sul codice 403)
    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model) {
        if (number == 403) {
            // Reindirizziamo l'utente alla Home, aggiungendo un query parameter che la Home intercetterà per mostrare l'errore
            return "redirect:/?notAuthorized";
        }
        return "redirect:/";
    }
}
