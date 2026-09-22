package aulab_chronicle.dtos;

import aulab_chronicle.models.Category;
import aulab_chronicle.models.User;
import aulab_chronicle.models.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String subtitle;
    private String body;
    private LocalDate publishDate;
    private User user;
    private Category category;
    private Image image;
    private Boolean isAccepted;
}
