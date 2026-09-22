package aulab_chronicle.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità DAO per la gestione delle immagini collegate agli articoli.
 * Memorizza il path dell'immagine salvata su Supabase Storage.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String path;

    // Relazione One-to-One / Many-to-One con l'Articolo
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;
}
