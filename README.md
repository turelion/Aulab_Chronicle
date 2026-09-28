# 📰 Aulab Chronicle — Piattaforma Editoriale Multiruolo

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-RBAC-blue.svg)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Supabase](https://img.shields.io/badge/Supabase-Image%20Storage-green.svg)](https://supabase.com/)
[![Mailtrap](https://img.shields.io/badge/Mailtrap-SMTP%20Testing-emeralds.svg)](https://mailtrap.io/)

**Aulab Chronicle** è una piattaforma editoriale web completa e strutturata, progettata per gestire un intero giornale digitale attraverso un workflow di pubblicazione multi-ruolo (Admin, Revisore, Redattore/Writer, Utente). 

Il progetto implementa un'architettura robusta basata su **Spring Boot 3**, **Spring Security**, **Thymeleaf** e **MySQL**, integrando servizi cloud per il media storage (**Supabase**) e l'invio asincrono delle email (**Mailtrap**).

---

## 🌟 Funzionalità Principali (Key Features)

### 👥 1. Autenticazione e Gestione Ruoli (RBAC)
- **Multi-Ruolo dinamico**: Gli utenti possono possedere più ruoli contemporaneamente (`ROLE_USER`, `ROLE_WRITER`, `ROLE_REVISOR`, `ROLE_ADMIN`).
- **Sistema "Lavora con noi"**: Form d'invio candidatura per diventare Writer o Revisor, con controllo preventivo anti-duplicati.
- **Protezione Rotte Avanzata**: Configurazione capillare in `SecurityConfig` per isolare aree e funzionalità riservate.

### ✍️ 2. Workflow Editoriale & Dashboard Scrittore (`ROLE_WRITER`)
- **Gestione Articoli (CRUD)**: Creazione, modifica e cancellazione dei propri articoli.
- **Programmazione della Pubblicazione (Scheduling)**:
  - Il Writer può impostare una data di pubblicazione futura.
  - L'articolo, anche se approvato dal Revisore, **rimane nascosto dal portale pubblico fino allo scattare della mezzanotte della data stabilita**.
- **Gestione Media Integrata**: Caricamento delle copertine su Supabase Storage e gestione automatica dell'immagine di fallback (`default.jpg`).

### 🔍 3. Workflow di Revisione & Dashboard Revisore (`ROLE_REVISOR`)
- **Gestione Stato Articoli**: Ogni articolo creato nasce con stato *In Revisione* (`is_accepted = null`).
- **Pannello di Controllo & Rettifica**:
  - Dashboard dedicata con lista degli articoli in attesa di revisione.
  - **Cronologia Revisioni**: Tabella riassuntiva degli ultimi articoli già revisionati, con la possibilità per il revisore di riaprire un articolo e **modificare la decisione presa** (Accetta / Rifiuta) in caso di errore.
- **Notifiche Badge**: Campanella in Navbar che segnala in tempo reale il numero di articoli in attesa.

### ⚙️ 4. Dashboard Amministratore (`ROLE_ADMIN`)
- **Gestione Candidature**: Approvazione delle richieste di collaborazione con assegnazione automatica del nuovo ruolo nel DB.
- **Gestione Categorie (CRUD)**: Creazione, modifica ed eliminazione delle categorie editoriali.
- **Campanella Notifiche**: Badge dinamico in Navbar con il conteggio delle candidature da gestire.

### 📧 5. Asincronia e Servizi Cloud
- **Supabase Storage**: Gestione del bucket cloud per il caricamento delle immagini e la pulizia sincrona/asincrona su cancellazione o sostituzione dell'immagine.
- **Email Asincrone (`@Async`)**: Invio non bloccante delle mail di notifica candidatura e conferma promozione tramite **Mailtrap SMTP**.

### 🔎 6. Ricerca & Navigazione
- **Ricerca Full-Text**: Form di ricerca globale in Navbar con query JPQL personalizzata su Titolo, Sottotitolo, Autore e Categoria.
- **Interfaccia e UX Personalizzata**: Styling responsive curato nei dettagli (Bootstrap 5 + foglio di stile CSS custom) per un'esperienza utente moderna ed intuitiva.

---

## 🛠️ Tech Stack & Strumenti

- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security (Role-Based Access Control)
- **Data Access**: Spring Data JPA / Hibernate
- **Database**: MySQL 8.0
- **Frontend Template**: Thymeleaf + HTML5, Bootstrap 5, Custom CSS, FontAwesome
- **Cloud Media Storage**: Supabase Bucket Storage (API REST)
- **Mail Server**: Mailtrap (SMTP Driver + Spring Mail)
- **DTO Mapping**: ModelMapper
- **Build Tool**: Maven

---

## 📂 Struttura del Database (Highlights)

- `users` & `roles`: Tabelle per la gestione delle utenze e dei permessi (relazione `@ManyToMany` tramite `users_roles`).
- `articles`: Contiene i dettagli dell'articolo (`title`, `subtitle`, `body`, `publish_date`, `is_accepted`, `user_id`, `category_id`).
- `categories`: Categorie dell'articolo.
- `images`: Mappatura dell'URL dell'immagine salvata su Supabase.
- `career_request`: Tracciamento delle candidature d'impiego per i ruoli di Writer e Revisor (`role_id`, `user_id`, `is_checked`, `body`).

---

## 🚀 Guida all'Installazione e Configurazione Locale

### 📋 Prerequisiti
- **Java JDK 17** o superiore installato.
- **Maven** installato (o wrapper `./mvnw` incluso).
- **MySQL Server** attivo localmente (es. via XAMPP, MySQL Workbench o Docker).
- Account gratuito su **Supabase** (Bucket pubblico per le immagini) e **Mailtrap** (Testing Inbox SMTP).

### 1. Clonare il Repository
```bash
git clone https://github.com/TUO_USERNAME/aulab-chronicle.git
cd aulab-chronicle
```

### 2. Configurare il Database MySQL
Crea un database MySQL dedicato (es. `aulab_chronicle`):
```sql
CREATE DATABASE aulab_chronicle;
```

Se necessario, popola il database con i ruoli iniziali e l'utente amministratore eseguendo lo script SQL fornito nel progetto (`insert.sql` / `schema.sql`).

### 3. Configurare `application.properties`
Apri il file `src/main/resources/application.properties` ed inserisci i tuoi parametri di configurazione:

```properties
# Server Port
server.port=8080

# Configurazione MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/aulab_chronicle?serverTimezone=UTC
spring.datasource.username=IL_TUO_USER_MYSQL
spring.datasource.password=LA_TUA_PASSWORD_MYSQL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configurazione Mailtrap SMTP
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=IL_TUO_USERNAME_MAILTRAP
spring.mail.password=LA_TUA_PASSWORD_MAILTRAP
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Configurazione Supabase Storage
supabase.url=https://IL_TUO_PROGETTO.supabase.co
supabase.key=LA_TUA_SUPABASE_ANON_KEY
supabase.bucket=IL_TUO_BUCKET_NAME
```

### 4. Avviare l'Applicazione
Lancia il progetto da terminale con Maven:
```bash
mvn spring-boot:run
```

L'applicazione sarà raggiungibile all'indirizzo: `http://localhost:8080`

---

## 🔑 Credenziali di Default per il Test

Se hai popolato il DB con lo script iniziale:

| Ruolo | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@aulab.it` | `12345678` |
| **User** | Registrati dal form `/register` | Inserita in registrazione |

---

## 👨‍💻 Autore

Sviluppato con passione come progetto finale per la specializzazione in Java-Spring per la Digital Factory Aulab.
- GitHub: [@turelion](https://github.com/turelion)
- LinkedIn: [Salvatore Leone](https://linkedin.com/in/salvatore-leone-full-stack-devback/)
