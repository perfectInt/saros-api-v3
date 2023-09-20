package ru.saros.sarosapiv3.domain.news;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class NewsMapper {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public NewsResponse toView(News news) {
        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setId(news.getId());
        newsResponse.setTitle(news.getTitle());
        newsResponse.setDescription(news.getDescription());
        newsResponse.setNewsDate(news.getNewsDate().format(FORMATTER));
        return newsResponse;
    }
}

