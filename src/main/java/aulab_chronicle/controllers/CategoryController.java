package aulab_chronicle.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import aulab_chronicle.dtos.ArticleDto;
import aulab_chronicle.dtos.CategoryDto;
import aulab_chronicle.models.Category;
import aulab_chronicle.services.ArticleService;
import aulab_chronicle.services.CategoryService;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    // Rotta per la ricerca degli articoli in base alla categoria [1]
    @GetMapping("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel) {
        CategoryDto category = categoryService.read(id);
        viewModel.addAttribute("title", "Tutti gli articoli trovati per categoria " + category.getName());

        List<ArticleDto> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));

        // Filtro per mostrare solo gli articoli accettati dal revisore [2]
        List<ArticleDto> acceptedArticles = articles.stream()
                .filter(article -> Boolean.TRUE.equals(article.getIsAccepted()))
                .toList();

        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }

    // Form creazione categoria
    @GetMapping("/create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea una Categoria");
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

    // Salva nuova categoria
    @PostMapping("")
    public String categoryStore(@Valid @ModelAttribute("category") Category category, BindingResult result,
            RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea una Categoria");
            return "category/create";
        }
        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria aggiunta con successo!");
        return "redirect:/admin/dashboard";
    }

    // Form modifica categoria
    @GetMapping("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Modifica Categoria");
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

    // Salva modifica categoria
    @PostMapping("/update/{id}")
    public String categoryUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category,
            BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Modifica Categoria");
            return "category/update";
        }
        categoryService.update(id, category, null);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria modificata con successo!");
        return "redirect:/admin/dashboard";
    }

    // Cancella categoria
    @GetMapping("/delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria eliminata con successo!");
        return "redirect:/admin/dashboard";
    }
}
