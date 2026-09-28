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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepo,
            UserRepository userRepo,
            CategoryRepository categoryRepo,
            ArticleRepository articleRepo,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Se il DB è vuoto, effettua il popolamento iniziale
            if (roleRepo.count() == 0) {

                // 1. CREAZIONE RUOLI
                Role adminRole = new Role(); adminRole.setName("ROLE_ADMIN");
                Role revisorRole = new Role(); revisorRole.setName("ROLE_REVISOR");
                Role writerRole = new Role(); writerRole.setName("ROLE_WRITER");
                Role userRole = new Role(); userRole.setName("ROLE_USER");

                roleRepo.saveAll(List.of(adminRole, revisorRole, writerRole, userRole));

                // 2. CREAZIONE UTENTI DI PROVA
                if (userRepo.count() == 0) {
                    User admin = new User();
                    admin.setUsername("Admin");
                    admin.setEmail("admin@aulabpost.it");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.getRoles().add(adminRole);

                    User revisor = new User();
                    revisor.setUsername("Revisore");
                    revisor.setEmail("revisore@aulabpost.it");
                    revisor.setPassword(passwordEncoder.encode("revisore123"));
                    revisor.getRoles().add(revisorRole);

                    User writer = new User();
                    writer.setUsername("Autore");
                    writer.setEmail("autore@aulabpost.it");
                    writer.setPassword(passwordEncoder.encode("autore123"));
                    writer.getRoles().add(writerRole);

                    User user = new User();
                    user.setUsername("Utente");
                    user.setEmail("utente@aulabpost.it");
                    user.setPassword(passwordEncoder.encode("utente123"));
                    user.getRoles().add(userRole);

                    userRepo.saveAll(List.of(admin, revisor, writer, user));

                    // 3. CREAZIONE CATEGORIE E ARTICOLI DI PROVA
                    if (categoryRepo.count() == 0) {
                        Category tech = new Category();
                        tech.setName("Tech");
                        tech = categoryRepo.save(tech);

                        Category lifestyle = new Category();
                        lifestyle.setName("Lifestyle");
                        lifestyle = categoryRepo.save(lifestyle);

                        Article art1 = new Article();
                        art1.setTitle("Il futuro dello Sviluppo Web con Spring Boot e Java");
                        art1.setSubtitle("Scopri le novità del framework nell'ecosistema moderno");
                        art1.setBody("Contenuto completo dell'articolo di prova per verificare la resa grafica della pagina di dettaglio...");
                        art1.setCategory(tech);
                        art1.setUser(writer);
                        art1.setIsAccepted(true);
                        art1.setPublishDate(LocalDate.now());

                        Article art2 = new Article();
                        art2.setTitle("Articolo in Attesa di Revisione");
                        art2.setSubtitle("Booster di produttività per sviluppatori junior");
                        art2.setBody("Questo articolo compare nella dashboard del revisore per testare l'accettazione o il rifiuto...");
                        art2.setCategory(lifestyle);
                        art2.setUser(writer);
                        art2.setIsAccepted(null);
                        art2.setPublishDate(LocalDate.now());

                        articleRepo.saveAll(List.of(art1, art2));
                    }
                }
            }
        };
    }
}