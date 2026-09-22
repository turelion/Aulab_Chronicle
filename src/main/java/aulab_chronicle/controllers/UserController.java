package aulab_chronicle.controllers;

import aulab_chronicle.dtos.ArticleDto;
import aulab_chronicle.dtos.UserDto;
import aulab_chronicle.models.Article;
import aulab_chronicle.models.User;
import aulab_chronicle.repositories.ArticleRepository;
import aulab_chronicle.repositories.CareerRequestRepository;
import aulab_chronicle.services.ArticleService;
import aulab_chronicle.services.CategoryService;
import aulab_chronicle.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Writer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Iniezione di ArticleService per recuperare gli articoli dell'utente
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Richieste ricevute");
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "admin/dashboard";
    }

    // Handler per la Home Page
    @GetMapping("/")
    public String home(Model viewModel) {
        List articles = new ArrayList<>();

        // Recupera solo gli articoli con is_accepted = true dal DB
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDto.class));
        }

        // Ordina gli articoli dal più recente al più vecchio basandosi sulla data di
        // pubblicazione
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());

        // Estrae solo i primi 3 articoli più recenti per la vetrina in Home
        List lastThreeArticles = articles.stream().limit(3).toList();

        viewModel.addAttribute("articles", lastThreeArticles);
        return "home";
    }

    // Handler per cercare gli articoli di un autore specifico
    @GetMapping("/search/{id}")
    public String userArticlesSearch(@PathVariable("id") Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "Articoli scritti da: " + user.getUsername());

        List<ArticleDto> articles = articleService.searchByAuthor(user);
        viewModel.addAttribute("articles", articles);

        return "article/articles";
    }

    // Handler GET per mostrare la form di Registrazione
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    // Handler POST per elaborare e salvare la registrazione
    @PostMapping("/register/save")
    public String registration(
            @Valid @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response) {

        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", "duplicate", "Esiste già un account registrato con questa email.");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        userService.saveUser(userDto, redirectAttributes, request, response);

        redirectAttributes.addFlashAttribute("successMessage",
                "Registrazione avvenuta con successo! Benvenuto in Aulab Chronicle.");

        return "redirect:/";
    }

    // Handler GET per mostrare la pagina di Login customizzata
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // Handler per la dashboard revisore: articoli da revisionare e ultimi 5 revisionati
    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Dashboard Revisore");

        // 1. Articoli ancora da revisionare (isAccepted == null)
        List<Article> pendingArticles = articleRepository.findByIsAcceptedIsNull();
        viewModel.addAttribute("pendingArticles", pendingArticles);

        // 2. Storico degli ultimi 5 articoli già revisionati (isAccepted != null)
        List<Article> reviewedArticles = articleRepository.findAll().stream()
                .filter(article -> article.getIsAccepted() != null)
                .sorted(Comparator.comparing(Article::getId).reversed())
                .limit(5) // <-- Limite agli ultimi 5
                .toList();

        viewModel.addAttribute("reviewedArticles", reviewedArticles);

        return "revisor/dashboard";
    }

    // rotta per mostrare al Writer solo i suoi articoli
    @GetMapping("/writer/dashboard")
    public String writerDashboard(Model viewModel, Principal principal) {
        viewModel.addAttribute("title", "I tuoi articoli");

        List<ArticleDto> userArticles = articleService.readAll().stream()
                .filter(article -> article.getUser().getEmail().equals(principal.getName()))
                .toList();

        viewModel.addAttribute("articles", userArticles);
        return "writer/dashboard";
    }
}