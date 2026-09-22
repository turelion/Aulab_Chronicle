package aulab_chronicle.repositories;

import aulab_chronicle.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Derived query: "SELECT * FROM roles WHERE name = ?"
    Role findByName(String name);
}