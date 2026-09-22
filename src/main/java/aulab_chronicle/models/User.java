package aulab_chronicle.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username; // Sarà popolato con "Nome Cognome"

    @Column(nullable = false, unique = true, length = 100)
    private String email; // Sarà l'identificativo reale per il login

    @Column(nullable = false)
    private String password;

    // Relazione Many-to-Many con i Ruoli.
    // Usiamo Eager Loading per assicurarci che i ruoli vengano letti subito all'autenticazione.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();
}
