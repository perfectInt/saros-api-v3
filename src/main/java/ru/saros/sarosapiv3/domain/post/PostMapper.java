package ru.saros.sarosapiv3.domain.post;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PostMapper {
    public Post toEntity(String title, String text) {
        Post post = new Post();
        post.setText(text);
        post.setTitle(title);
        return post;
    }

    public PostView toView(Post post) {
        return new PostView(post.getId(), post.getTitle(), post.getText(), post.getImageId(), post.getPostDate());
    }
}
