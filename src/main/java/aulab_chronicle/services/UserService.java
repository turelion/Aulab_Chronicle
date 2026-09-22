package aulab_chronicle.services;

import aulab_chronicle.dtos.UserDto;
import aulab_chronicle.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface UserService {
    // Registra un nuovo utente con login automatico
    void saveUser(UserDto userDto, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response);

    // Recupera l'utente in base all'email (usato per i controlli di unicità)
    User findUserByEmail(String email);

    // Recupera l'utente in base all'ID (per future ricerche/relazioni)
    User find(Long id);
}
