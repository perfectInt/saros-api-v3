package ru.saros.sarosapiv3.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.saros.sarosapiv3.api.exception.PostNotFoundException;
import ru.saros.sarosapiv3.domain.image.Image;
import ru.saros.sarosapiv3.domain.image.ImageMapper;
import ru.saros.sarosapiv3.domain.image.ImageRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;


    @Transactional
    public PostView createPost(String title, String text, MultipartFile file) throws IOException {
        Post post = postMapper.toEntity(title, text);
        Image image = imageMapper.toEntity(file);
        Long imageId = imageRepository.save(image).getId();
        post.setImageId(imageId);
        post.setPostDate(LocalDateTime.now());
        return postMapper.toView(postRepository.save(post));
    }

    public List<PostView> getAllPosts() {
        List<PostView> postViews = new ArrayList<>();
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            postViews.add(postMapper.toView(post));
        }
        return postViews;
    }

    public PostView getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Could not find post with this id"));
        return postMapper.toView(post);
    }
}
