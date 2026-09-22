package aulab_chronicle.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotEmpty(message = "Il titolo è obbligatorio")
    @Size(max = 100, message = "Il titolo non può superare i 100 caratteri")
    private String title;

    @Column(nullable = false, length = 100)
    @NotEmpty(message = "Il sottotitolo è obbligatorio")
    @Size(max = 100, message = "Il sottotitolo non può superare i 100 caratteri")
    private String subtitle;

    @Column(nullable = false, length = 1000)
    @NotEmpty(message = "Il corpo dell'articolo è obbligatorio")
    @Size(max = 1000, message = "Il testo non può superare i 1000 caratteri")
    private String body;

    @Column(nullable = true)
    @NotNull(message = "La data di pubblicazione è obbligatoria")
    private LocalDate publishDate;

    // Relazione Many-to-One con User (Autore dell'articolo)
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "articles" })
    private User user;

    // Relazione Many-to-One con Category (Categoria dell'articolo)
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({ "articles" })
    private Category category;

    @OneToOne(mappedBy = "article")
    @JsonIgnoreProperties({ "article" })
    private Image image;

    // Stato revisione articolo: null = in attesa/pendente, true = approvato, false
    // = rifiutato
    @Column(nullable = true)
    private Boolean isAccepted;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Article article = (Article) obj;

        return Objects.equals(title, article.title) &&
                Objects.equals(subtitle, article.subtitle) &&
                Objects.equals(body, article.body) &&
                Objects.equals(category, article.category);
    }
}
