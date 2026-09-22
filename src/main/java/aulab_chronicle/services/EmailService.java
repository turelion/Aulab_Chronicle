package aulab_chronicle.services;

public interface EmailService {
    // Metodo per inviare un'e-mail di solo testo
    // String to: L'indirizzo e-mail del destinatario
    // String subject: L'oggetto dell'e-mail
    // String text: Il contenuto dell'e-mail
    void sendSimpleEmail(String to, String subject, String text);
}
