package aulab_chronicle.services;

import java.security.Principal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaccia Generica per i servizi CRUD dell'applicazione.
 * @param <ReadDto> Il DTO restituito in lettura
 * @param <Model> L'Entità DAO gestita in scrittura
 * @param <Key> La chiave primaria dell'Entità (es. Long o Integer)
 */
public interface CrudService<ReadDto, Model, Key> {
    List<ReadDto> readAll();
    ReadDto read(Key key);
    ReadDto create(Model model, Principal principal, MultipartFile file);
    ReadDto update(Key key, Model model, MultipartFile file);
    void delete(Key key);
}