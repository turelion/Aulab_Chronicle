package aulab_chronicle.controllers;

import aulab_chronicle.dtos.ArticleDto;
import aulab_chronicle.models.Article;
import aulab_chronicle.repositories.ArticleRepository;
import aulab_chronicle.services.ArticleService;
import aulab_chronicle.services.CrudService;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Handler per la pagina con l'elenco completo degli articoli pubblicati (GET
    // /articles)
    @GetMapping("")
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli");

        List<ArticleDto> articles = new ArrayList<>();

        // Recupera dal DB solo gli articoli approvati e la cui data di pubblicazione è
        // giunta (publishDate <= oggi)
        for (Article article : articleRepository.findPublishedArticles(java.time.LocalDate.now())) {
            articles.add(modelMapper.map(article, ArticleDto.class));
        }

        // Ordina la lista degli articoli dal più recente al più vecchio
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());

        viewModel.addAttribute("articles", articles);
        return "article/articles";
    }

    // Handler per mostrare la form di creazione (GET /articles/create)
    @GetMapping("/create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea un articolo");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }

    // Handler per il salvataggio dell'articolo (POST /articles)
    @PostMapping
    public String articleStore(@Valid @ModelAttribute("article") Article article,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Principal principal,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model viewModel) {

        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un articolo");
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }

        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo aggiunto con successo!");

        return "redirect:/";
    }

    // Handler per mostrare la pagina di dettaglio di un singolo articolo (GET
    // /articles/detail/{id})
    @GetMapping("/detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio Articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }

    // Rotta dettaglio articolo per il revisore (GET /articles/revisor/detail/{id})
    @GetMapping("/revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio Articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    // Rotta per l'azione di accettazione o rifiuto da parte del revisore (POST
    // /articles/accept)
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action,
            @RequestParam("articleId") Long articleId,
            RedirectAttributes redirectAttributes) {
        if ("accept".equals(action)) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo accettato e pubblicato con successo!");
        } else if ("reject".equals(action)) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo rifiutato!");
        }
        return "redirect:/revisor/dashboard";
    }

    // Mostra la form di modifica con i dati dell'articolo corrente (GET
    // /articles/edit/{id})
    @GetMapping("/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Modifica Articolo");
        viewModel.addAttribute("article", articleService.read(id));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/edit";
    }

    // Salvataggio modifica articolo (POST /articles/update/{id})
    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id,
            @Valid @ModelAttribute("article") Article article,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model viewModel) {

        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Modifica Articolo");
            viewModel.addAttribute("categories", categoryService.readAll());
            viewModel.addAttribute("article", articleService.read(id));
            return "article/edit";
        }

        articleService.update(id, article, file);
        redirectAttributes.addFlashAttribute("successMessage",
                "Articolo modificato con successo! È tornato in revisione.");
        return "redirect:/writer/dashboard";
    }

    // Cancellazione articolo e rimozione dell'immagine dal cloud (GET
    // /articles/delete/{id})
    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo cancellato con successo!");
        return "redirect:/writer/dashboard";
    }

    // Rotta per la ricerca degli articoli (GET /articles/search?keyword=...)
    @GetMapping("/search")
    public String articleSearch(@RequestParam("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "Risultati della ricerca per: " + keyword);
        viewModel.addAttribute("articles", articleService.search(keyword));
        return "article/articles";
    }
}
