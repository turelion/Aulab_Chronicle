package aulab_chronicle.repositories;

import aulab_chronicle.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    //Cancellazione nativa personalizzata basata sul path completo. Richiede @Transactional e @Modifying per garantire l'integrità della transazione.
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM images WHERE path = :path", nativeQuery = true)
    void deleteByPath(@Param("path") String path);
}
