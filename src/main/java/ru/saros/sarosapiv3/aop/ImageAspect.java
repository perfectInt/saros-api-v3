package ru.saros.sarosapiv3.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.saros.sarosapiv3.api.exception.ImageNotFoundException;

@Aspect
@Component
@Slf4j
public class ImageAspect {

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.api.controller.ImageController.getImageById(..))",
            throwing = "ex"
    )
    public void imageWasNotFound(JoinPoint jp, ImageNotFoundException ex) {
        log.warn("Cannot find image with id=" + jp.getArgs()[0]);
        log.warn("Exception's message: " + ex.getMessage());
    }
}
