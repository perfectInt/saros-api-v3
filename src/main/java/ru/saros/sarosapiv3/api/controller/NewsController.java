package ru.saros.sarosapiv3.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.saros.sarosapiv3.domain.news.News;
import ru.saros.sarosapiv3.domain.news.NewsResponse;
import ru.saros.sarosapiv3.domain.news.NewsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/news")
@CrossOrigin
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsResponse getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NewsResponse> getNewsByPage(@RequestParam(name = "page", required = false) Integer page) {
        return newsService.getNewsByPages(page);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public News createNews(@RequestBody News news) {
        return newsService.createNews(news);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public void updateNews(@RequestBody News news) {
        newsService.updateNews(news);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }
}
