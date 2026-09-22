package aulab_chronicle.services;

import aulab_chronicle.models.Article;
import aulab_chronicle.models.Image;
import aulab_chronicle.repositories.ImageRepository;
import aulab_chronicle.utils.StringManipulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String supabaseBucket;

    @Value("${supabase.image}")
    private String supabaseImage;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void saveImageOnDB(String url, Article article) {
        String publicUrl = url.replace(supabaseBucket, supabaseImage);
        imageRepository.save(Image.builder().path(publicUrl).article(article).build());
    }

    /**
     * Caricamento asincrono su Supabase Cloud.
     * Genera un UUID univoco per evitare sovrascritture di file con lo stesso nome.
     */
    @Async
    @Override
    public CompletableFuture<String> saveImageOnCloud(MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            try {
                String nameFile = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                String extension = StringManipulation.getFileExtension(nameFile);
                String url = supabaseUrl + supabaseBucket + nameFile;

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "image/" + extension);
                headers.set("Authorization", "Bearer " + supabaseKey);

                HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
                restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

                return CompletableFuture.completedFuture(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CompletableFuture.failedFuture(new IllegalArgumentException("File vuoto o non valido"));
    }

    @Async
    @Transactional
    @Override
    public void deleteImage(String imagePath) throws IOException {
        String url = imagePath.replace(supabaseImage, supabaseBucket);
        imageRepository.deleteByPath(imagePath);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }
}
