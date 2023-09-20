package ru.saros.sarosapiv3.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UserAspect {

    @AfterThrowing(
            pointcut = "execution(* ru.saros.sarosapiv3.api.controller.UserController.getAllUsers())",
            throwing = "ex"
    )
    public void ifSomeoneWithoutAccessIsTryingToGetInformationAboutAllUsers(Exception ex) {
        log.warn("Someone without authority grants is trying to get information about users!\n" +
                "Exception message: " + ex.getMessage());
    }
}
