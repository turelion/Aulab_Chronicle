package aulab_chronicle.services;

import aulab_chronicle.models.Role;
import aulab_chronicle.models.User;
import aulab_chronicle.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Cerchiamo l'utente tramite l'email passata nel form
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Credenziali non valide.");
        }

        // Restituiamo il CustomUserDetails con ID, Nome Completo, Email, Password e Ruoli mappati
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    // Trasforma i ruoli memorizzati nel DB in GrantedAuthority accettate da Spring Security
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            // Ruolo di default di emergenza se l'utente non ha ruoli assegnati
            return java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
