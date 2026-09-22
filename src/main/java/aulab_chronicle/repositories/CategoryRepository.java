package aulab_chronicle.repositories;

import aulab_chronicle.models.Category;
import org.springframework.data.repository.ListCrudRepository;

// Rimuovo @Repository perché ListCrudRepository è già annotato con @Repository, quindi non è necessario aggiungerlo di nuovo.
public interface CategoryRepository extends ListCrudRepository<Category, Long> {
}
