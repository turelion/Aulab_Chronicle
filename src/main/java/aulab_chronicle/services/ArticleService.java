package aulab_chronicle.services;

import aulab_chronicle.dtos.ArticleDto;
import aulab_chronicle.models.Article;
import aulab_chronicle.models.User;
import aulab_chronicle.models.Category;
import aulab_chronicle.repositories.ArticleRepository;
import aulab_chronicle.repositories.ImageRepository;
import aulab_chronicle.repositories.UserRepository;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service("articleService")
public class ArticleService implements CrudService<ArticleDto, Article, Long> {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public List<ArticleDto> readAll() {
        List<ArticleDto> dtos = new ArrayList<>();
        for (Article article : articleRepository.findAll()) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    @Override
    public ArticleDto read(Long key) {
        Optional<Article> optArticle = articleRepository.findById(key);
        if (optArticle.isPresent()) {
            return modelMapper.map(optArticle.get(), ArticleDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato con id: " + key);
        }
    }

    @Override
    public ArticleDto create(Article article, Principal principal, MultipartFile file) {
        String url = "";

        // 1. Recupero dell'utente autenticato dalla sessione
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            article.setUser(user);
        }

        // 2. Caricamento asincrono su Supabase Cloud
        if (file != null && !file.isEmpty()) {
            try {
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                url = futureUrl.get(); // Blocco controllato in attesa dell'URL da Supabase
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Imposta l'articolo come "in attesa di revisione" prima del salvataggio nel DB
        article.setIsAccepted(null);

        // 3. Salvataggio dell'articolo nel DB MySQL
        Article savedArticle = articleRepository.save(article);
        ArticleDto dto = modelMapper.map(savedArticle, ArticleDto.class);

        // 4. Salvataggio dell'immagine nel DB collegandola all'articolo appena salvato
        if (file != null && !file.isEmpty() && !url.isEmpty()) {
            imageService.saveImageOnDB(url, savedArticle);
        }

        return dto;
    }

    // Modifica l'articolo: se vengono cambiati i testi o l'immagine, l'articolo
    // torna "in revisione" (null)
    @Override
    public ArticleDto update(Long key, Article updatedArticle, MultipartFile file) {
        if (articleRepository.existsById(key)) {
            Article originalArticle = articleRepository.findById(key).get();
            updatedArticle.setId(key);
            updatedArticle.setUser(originalArticle.getUser());

            // Gestione eventuale nuova immagine
            if (!file.isEmpty()) {
                try {
                    if (originalArticle.getImage() != null) {
                        imageService.deleteImage(originalArticle.getImage().getPath());
                    }
                    CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                    String url = futureUrl.get();
                    imageService.saveImageOnDB(url, updatedArticle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                updatedArticle.setImage(originalArticle.getImage());
            }

            // Se l'articolo è stato modificato, torna in revisione
            if (!updatedArticle.equals(originalArticle) || !file.isEmpty()) {
                updatedArticle.setIsAccepted(null);
            } else {
                updatedArticle.setIsAccepted(originalArticle.getIsAccepted());
            }

            return modelMapper.map(articleRepository.save(updatedArticle), ArticleDto.class);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    // Elimina l'articolo dal DB insieme alla sua immagine salvata su Cloud Storage
    @Override
    @Transactional
    public void delete(Long key) {
        if (articleRepository.existsById(key)) {
            Article article = articleRepository.findById(key).get();
            try {
                if (article.getImage() != null) {
                    String path = article.getImage().getPath();

                    // 1. Cancelliamo il record dell'immagine dal DB in modo sincrono
                    imageRepository.deleteByPath(path);

                    // 2. Avviamo la cancellazione asincrona del file fisico su Supabase
                    imageService.deleteImage(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3. Eliminiamo l'articolo senza blocchi di chiave esterna
            articleRepository.deleteById(key);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public List<ArticleDto> searchByCategory(Category category) {
        List<ArticleDto> dtos = new ArrayList<>();
        for (Article article : articleRepository.findByCategory(category)) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    public List<ArticleDto> searchByAuthor(User user) {
        List<ArticleDto> dtos = new ArrayList<>();
        List<Article> articles = articleRepository.findByUser(user);
        for (Article article : articles) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    public void setIsAccepted(Boolean result, Long id) {
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

    public List<ArticleDto> search(String keyword) {
        List<ArticleDto> dtos = new ArrayList<>();
        // Cerca solo tra gli articoli accettati e la cui data è <= oggi
        for (Article article : articleRepository.searchPublished(keyword, java.time.LocalDate.now())) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }
}
