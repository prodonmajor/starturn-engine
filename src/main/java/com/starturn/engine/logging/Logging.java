/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.logging;

import com.starturn.engine.facade.IAuthenticationFacade;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Administrator
 */
@Aspect
@Configuration
public class Logging {
    private @Autowired IAuthenticationFacade authenticationFacade;
    private static final Logger logger = LogManager.getLogger(Logging.class);
    
    @Before("execution(* com.easycoop.radicalengineaccount.controller.businesslogic.*.*(..))")
    public void logEventBefore(JoinPoint joinPoint) {
        String user = "[non-authenticated-user]";
        if (authenticationFacade.getAuthentication() != null) {
            user = authenticationFacade.getAuthentication().getName();
        }
        
        Signature signature = joinPoint.getSignature();
        
        String methodName = signature.getName();
        String methodSignature = signature.toString();
        String methodArguments = Arrays.toString(joinPoint.getArgs());
        
        logger.info("Processing request from user: {}, for method: {}, with method signature: {}, and method arguments: {}", user, methodName, methodSignature, methodArguments);
    }
    
    @After("execution(* com.easycoop.radicalengineaccount.controller.businesslogic.*.*(..))")
    public void logEventAfter(JoinPoint joinPoint) {
        String user = "[non-authenticated-user]";
        if (authenticationFacade.getAuthentication() != null) {
            user = authenticationFacade.getAuthentication().getName();
        }
        
        Signature signature = joinPoint.getSignature();
        
        String methodName = signature.getName();
        String methodSignature = signature.toString();
        String methodArguments = Arrays.toString(joinPoint.getArgs());
        
        logger.info("Completed request from user: {}, for method: {}, with method signature: {}, and method arguments: {}", user, methodName, methodSignature, methodArguments);
    }
    
    @AfterThrowing(
            pointcut="execution(* com.easycoop.radicalengineaccount.controller.businesslogic.*.*(..))",
            throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String user = "[non-authenticated-user]";
        if (authenticationFacade.getAuthentication() != null) {
            user = authenticationFacade.getAuthentication().getName();
        }
        
        Signature signature = joinPoint.getSignature();
        
        String methodName = signature.getName();
        String methodSignature = signature.toString();
        String methodArguments = Arrays.toString(joinPoint.getArgs());
        
        logger.error("Error thrown on request from user: {}, for method: {}, with method signature: {}, and method arguments: {}", user, methodName, methodSignature, methodArguments, ex);
    }
}

