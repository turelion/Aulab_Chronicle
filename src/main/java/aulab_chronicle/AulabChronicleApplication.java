package aulab_chronicle;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableTransactionManagement // Abilita il supporto globale per le transazioni SQL
@EnableAsync(proxyTargetClass = true)
public class AulabChronicleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AulabChronicleApplication.class, args);
	}

	// Registra l'encoder BCrypt come Bean globale in memoria
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Registra ModelMapper per le future conversioni DTO-DAO
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
