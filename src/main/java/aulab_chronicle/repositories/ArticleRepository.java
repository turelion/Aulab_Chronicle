package aulab_chronicle.repositories;

import aulab_chronicle.models.Article;
import aulab_chronicle.models.Category;
import aulab_chronicle.models.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ArticleRepository extends ListCrudRepository<Article, Long> {
    List<Article> findByCategory(Category category);

    List<Article> findByUser(User user);

    List<Article> findByIsAcceptedTrue();

    // Query per gli articoli pubblicati e già visibili alla data attuale
    @Query("SELECT a FROM Article a WHERE a.isAccepted = true AND a.publishDate <= :today")
    List<Article> findPublishedArticles(@Param("today") LocalDate today);

    List<Article> findByIsAcceptedFalse();

    List<Article> findByIsAcceptedIsNull();

    @Query("SELECT a FROM Article a WHERE a.isAccepted = true AND a.publishDate <= :today AND (" +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.subtitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Article> searchPublished(@Param("searchTerm") String searchTerm, @Param("today") LocalDate today);
}
