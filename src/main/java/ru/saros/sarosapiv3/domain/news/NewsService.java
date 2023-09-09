package ru.saros.sarosapiv3.domain.news;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.saros.sarosapiv3.api.exception.NewsNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    public News createNews(News news) {
        news.setNewsDate(LocalDateTime.now());
        return newsRepository.save(news);
    }

    public List<NewsResponse> getNewsByPages(Integer page) {
        if (page == null) page = 0;
        Pageable paging = PageRequest.of(page, 10, Sort.by("newsDate").descending());
        Page<News> news = newsRepository.findAll(paging);
        List<NewsResponse> newsResponses = new ArrayList<>();
        for (News n : news) {
            NewsResponse newsResponse = newsMapper.toView(n);
            newsResponses.add(newsResponse);
        }
        return newsResponses;
    }

    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NewsNotFoundException("Cannot find this news"));
        return newsMapper.toView(news);
    }

    public void updateNews(News news) {
        newsRepository.save(news);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}
