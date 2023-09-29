package ru.saros.sarosapiv3.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.saros.sarosapiv3.domain.post.PostService;
import ru.saros.sarosapiv3.domain.post.PostView;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/posts")
@CrossOrigin
@Tag(name = "Post controller", description = "Endpoints for posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    @Operation(tags = "Post controller", description = "Create new post")
    public PostView createPost(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("image") MultipartFile file
            ) throws IOException {
        return postService.createPost(title, text, file);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(tags = "Post controller", description = "Get all posts")
    public List<PostView> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(tags = "Post controller", description = "Get post by its id")
    public PostView getPostById(@PathVariable Long id) {
        return postService.getPost(id);
    }
}
