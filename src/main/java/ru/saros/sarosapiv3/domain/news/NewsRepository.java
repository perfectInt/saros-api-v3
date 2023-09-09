package ru.saros.sarosapiv3.domain.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}

