package aulab_chronicle.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "Il nome della categoria non può essere vuoto")
    @Size(max = 50, message = "Il nome non può superare i 50 caratteri")
    private String name;

    // Relazione One-to-Many: Una categoria appartiene a più articoli
    @OneToMany(mappedBy = "category")
    private List<Article> articles = new ArrayList<>();
}