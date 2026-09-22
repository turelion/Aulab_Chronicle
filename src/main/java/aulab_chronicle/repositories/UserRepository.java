package aulab_chronicle.repositories;

import aulab_chronicle.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Derived query automatica che genera una query SQL: "SELECT * FROM users WHERE email = ?"
    User findByEmail(String email);
}
