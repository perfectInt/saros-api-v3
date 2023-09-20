package ru.saros.sarosapiv3.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.saros.sarosapiv3.api.exception.PasswordException;
import ru.saros.sarosapiv3.api.exception.UserAlreadyExistsException;
import ru.saros.sarosapiv3.domain.user.User;

@Aspect
@Component
@Slf4j
public class AuthAspect {

    @Before("execution(* ru.saros.sarosapiv3.domain.user.UserService.registerUser(..))")
    public void beforeUserRegistration() {
        log.info("Processing user's info before registration");
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.user.UserService.registerUser(..))",
            throwing = "ex"
    )
    public void ifUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("Exception's message: " + ex.getMessage());
    }


    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.user.UserService.registerUser(..))",
            throwing = "ex"
    )
    public void ifPasswordsAreNotMatchingDuringTheRegistration(PasswordException ex) {
        log.warn("Exception's message: " + ex.getMessage());
    }

    @AfterReturning(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.user.UserService.registerUser(..))",
            returning = "user"
    )
    public void afterUserRegistration(User user) {
        log.info("User with email=" + user.getEmail() + " was registered successfully");
    }

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.domain.user.UserService.login(..))",
            throwing = "ex"
    )
    public void ifUserCannotLogin(Exception ex) {
        log.warn(ex.getMessage());
    }
}