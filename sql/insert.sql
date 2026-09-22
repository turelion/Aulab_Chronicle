-- Inserimento dei Ruoli Applicativi di Default
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_REVISOR'),
('ROLE_WRITER'),
('ROLE_USER');

-- Creazione Utente Amministratore Iniziale (username: admin, email: admin@aulab.it, password: 12345678)
INSERT INTO users (username, email, password, created_at) VALUES
('admin', 'admin@aulab.it', '$2a$10$oMiUOq5ToRfUI/Zprg5nE.qt8nT9KKJZoDBu1SIWuj.UGx8aRHwxS', '20240607');

-- Associazione dell'utente Amministratore (id: 1) al ruolo ROLE_ADMIN (id: 1)
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);

-- Inserimento delle Categorie Editoriali Iniziali
INSERT INTO categories (name) VALUES
('politica'),
('economia'),
('food&drink'),
('sport'),
('intrattenimento'),
('tech');