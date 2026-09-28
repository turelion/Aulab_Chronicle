package aulab_chronicle.config;

import aulab_chronicle.models.Category;
import aulab_chronicle.models.Article;
import aulab_chronicle.models.Role;
import aulab_chronicle.models.User;
import aulab_chronicle.repositories.ArticleRepository;
import aulab_chronicle.repositories.CategoryRepository;
import aulab_chronicle.repositories.RoleRepository;
import aulab_chronicle.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("demo")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepo,
            UserRepository userRepo,
            CategoryRepository categoryRepo,
            ArticleRepository articleRepo,
            PasswordEncoder passwordEncoder,
            TransactionTemplate transactionTemplate) {

        return args -> transactionTemplate.executeWithoutResult(status -> {
            if (roleRepo.count() == 0) {

                // ... qui incolla tutto il corpo che hai già
                // (dalla creazione dei ruoli fino agli articoli),
                // senza cambiare nulla

            }
        });
    }
}
