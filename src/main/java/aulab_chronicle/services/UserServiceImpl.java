package aulab_chronicle.services;

import aulab_chronicle.dtos.UserDto;
import aulab_chronicle.models.Role;
import aulab_chronicle.models.User;
import aulab_chronicle.repositories.RoleRepository;
import aulab_chronicle.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void saveUser(UserDto userDto, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        // Uniamo Nome e Cognome nel campo username dell'entità
        user.setUsername(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // Crittografia della password tramite il passwordEncoder configurato a livello globale
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Assegnazione automatica del ruolo base 'ROLE_USER' al momento dell'iscrizione
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(role));

        // Salvataggio nel database
        userRepository.save(user);

        // Effettua l'autenticazione istantanea senza richiedere un nuovo inserimento di credenziali
        authenticateUserAndSetSession(user, userDto, request);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User find(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Autentica manualmente un utente appena registrato e memorizza il token di autenticazione nella sessione corrente.
     */
    private void authenticateUserAndSetSession(User user, UserDto userDto, HttpServletRequest request) {
        try {
            // Carica il CustomUserDetails relativo all'email appena salvata
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getEmail());

            // Crea il token di autenticazione usando l'email come login e la password in chiaro del DTO
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDto.getPassword()
            );

            // Chiede all'AuthenticationManager di validare l'autenticazione
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Inserisce l'utente autenticato all'interno del contesto di sicurezza globale di Spring Security
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Crea o ottiene la sessione HTTP e vi memorizza il contesto di sicurezza per mantenere attivo il login
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        } catch (Exception e) {
            // Registra a terminale l'errore per il debug ma non blocca l'applicazione
            System.err.println("Errore durante il login automatico post-registrazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
