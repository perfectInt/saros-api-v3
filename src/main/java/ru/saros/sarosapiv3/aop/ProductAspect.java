package ru.saros.sarosapiv3.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.saros.sarosapiv3.domain.product.Product;

@Aspect
@Component
@Slf4j
public class ProductAspect {

    @Before("execution(* ru.saros.sarosapiv3.domain.product.ProductService.saveProduct(..))")
    public void beforeProductCreating() {
        log.info("Processing product before creating it");
    }

    @AfterReturning(
            pointcut=("execution(* ru.saros.sarosapiv3.domain.product.ProductService.saveProduct(..))"),
            returning="product"
    )
    public void ifProductHasBeenCreated(Product product) {
        log.info("The product with id=" + product.getId() + " has been created successfully");
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.product.ProductService.saveProduct(..))",
            throwing = "ex"
    )
    public void ifProductWasNotCreated(Exception ex) {
        log.error("It occurred to some errors while saving a product");
        log.error("Exception message: " + ex.getMessage());
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.product.ProductService.getProductById(..))"
    )
    public void ifProductWasNotFound(JoinPoint jp) {
        log.warn("Cannot find any product with id=" + jp.getArgs()[0]);
    }

    @After("execution(* ru.saros.sarosapiv3.domain.product.ProductService.deleteProduct(..))")
    public void afterProductDeleting(JoinPoint jp) {
        log.info("Deleted a product with id=" + jp.getArgs()[0]);
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.api.controller.ProductController.createProduct(..))"
    )
    public void ifProductsParamsAreIncorrect() {
        log.error("Incorrect params");
    }
}
