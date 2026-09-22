package aulab_chronicle.config;

import aulab_chronicle.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Definiamo il Bean globale AuthenticationManager necessario per l'auto-login del service utente
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disabilitiamo CSRF solo se strettamente necessario allo sviluppo locale (scelta del docente)
                .csrf(csrf -> csrf.disable())

                // Configurazione dei permessi di accesso per le rotte URL (Autorizzazioni)
                .authorizeHttpRequests(authorize -> authorize
                        // Rotte riservate all'ADMIN
                        .requestMatchers("/admin/dashboard", "/categories/create", "/categories/edit/{id}", "/categories/update/{id}", "/categories/delete/{id}").hasRole("ADMIN")

                        // Rotte riservate al REVISOR
                        .requestMatchers("/revisor/dashboard", "/revisor/detail/{id}", "/articles/accept").hasRole("REVISOR")

                        // Rotte riservate al WRITER
                        .requestMatchers("/writer/dashboard", "/articles/create", "/articles/edit/{id}", "/articles/update/{id}", "/articles/delete/{id}").hasRole("WRITER")

                        // Rotte accessibili a tutti (pubbliche)
                        .requestMatchers("/register/**", "/", "/articles", "/images/**", "/css/**", "/js/**", "/articles/detail/**", "/categories/search/{id}", "/search/{id}", "/articles/search", "/operations/career/request", "/operations/career/request/save").permitAll()

                        // Ogni altra rotta non esplicitamente elencata richiederà l'autenticazione
                        .anyRequest().authenticated()
                )

                // Personalizzazione del Form di Login
                .formLogin(form -> form
                        .loginPage("/login") // Endpoint del nostro form custom (GET)
                        .loginProcessingUrl("/login") // Endpoint di ricezione dei dati di login (POST gestito da Security)
                        .defaultSuccessUrl("/") // Redirect alla Home post-login con successo
                        .permitAll()
                )

                // Personalizzazione della procedura di Logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // <-- Uso logoutUrl invece di logoutRequestMatcher
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // Gestione degli Errori di Autorizzazione
                .exceptionHandling(exception -> exception
                        // Quando un utente loggato prova a forzare a mano un URL non autorizzato, lo rimandiamo all'ExceptionController
                        .accessDeniedPage("/error/403")
                )

                // Gestione dello stato delle sessioni
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Crea una sessione solo se necessaria
                        .maximumSessions(1) // Limita a 1 sola sessione attiva simultanea per utente
                        .expiredUrl("/login?session-expired=true") // Se la sessione scade, reindirizza
                );

        return http.build();
    }

    // Configura globalmente il servizio di lettura utenti e l'encoder per il confronto password
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}