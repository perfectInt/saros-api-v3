package ru.saros.sarosapiv3.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.saros.sarosapiv3.domain.news.News;

@Aspect
@Component
@Slf4j
public class NewsAspect {

    @Before("execution(* ru.saros.sarosapiv3.domain.news.NewsService.createNews(..))")
    public void beforeNewsCreating() {
        log.info("Processing news before creating it");
    }

    @AfterReturning(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.news.NewsService.createNews(..))",
            returning="news"
    )
    public void ifNewsHasBeenCreated(News news) {
        log.info("The news with id=" + news.getId() + " has been created successfully");
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.news.NewsService.createNews(..))",
            throwing = "ex"
    )
    public void ifNewsWasNotCreated(Exception ex) {
        log.error("It occurred to some errors while saving a product");
        log.error("Exception message: " + ex.getMessage());
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.news.NewsService.getNewsById())"
    )
    public void ifNewsWasNotFound(JoinPoint jp) {
        log.warn("Cannot find any news with id=" + jp.getArgs()[0]);
    }

    @After("execution(* ru.saros.sarosapiv3.domain.news.NewsService.deleteNews(..))")
    public void afterProductDeleting(JoinPoint jp) {
        log.info("Deleted a news with id=" + jp.getArgs()[0]);
    }
}
