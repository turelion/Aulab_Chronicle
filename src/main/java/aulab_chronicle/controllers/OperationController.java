package aulab_chronicle.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aulab_chronicle.models.CareerRequest;
import aulab_chronicle.models.Role;
import aulab_chronicle.models.User;
import aulab_chronicle.repositories.RoleRepository;
import aulab_chronicle.repositories.UserRepository;
import aulab_chronicle.services.CareerRequestService;

@Controller
@RequestMapping("/operations")
public class OperationController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRequestService careerRequestService;

    // Form per inviare la candidatura "Lavora con noi"
    @GetMapping("/career/request")
    public String careerRequestCreate(Model viewModel) {
        viewModel.addAttribute("title", "Inserisci la tua richiesta");
        viewModel.addAttribute("careerRequest", new CareerRequest());

        List<Role> roles = roleRepository.findAll();
        // Rimuove il ruolo base USER dalla lista selezioni
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));
        viewModel.addAttribute("roles", roles);

        return "career/requestForm";
    }

    // Salvataggio della candidatura
    @PostMapping("/career/request/save")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest, Principal principal,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(principal.getName());

        if (careerRequestService.isRoleAlreadyAssigned(user, careerRequest)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Sei già assegnato a questo ruolo o hai inviato una richiesta");
            return "redirect:/";
        }

        careerRequestService.save(careerRequest, user);
        redirectAttributes.addFlashAttribute("successMessage", "Richiesta inviata con successo!");

        return "redirect:/";
    }

    // Dettaglio della candidatura per l'Admin
    @GetMapping("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio richiesta");
        viewModel.addAttribute("request", careerRequestService.find(id));
        return "career/requestDetail";
    }

    // Accettazione della candidatura da parte dell'Admin
    @PostMapping("/career/request/accept/{requestId}")
    public String careerRequestAccept(@PathVariable("requestId") Long requestId,
            RedirectAttributes redirectAttributes) {
        careerRequestService.careerAccept(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Ruolo abilitato per l'utente!");
        return "redirect:/admin/dashboard";
    }

    // Rifiuto della candidatura da parte dell'Admin
    @PostMapping("/career/request/reject/{requestId}")
    public String careerRequestReject(@PathVariable("requestId") Long requestId,
            RedirectAttributes redirectAttributes) {
        careerRequestService.careerReject(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Richiesta rifiutata ed eliminata con successo!");
        return "redirect:/admin/dashboard";
    }
}