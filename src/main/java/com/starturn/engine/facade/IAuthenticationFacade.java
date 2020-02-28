/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.facade;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Idoko Emmanuel
 */
public interface IAuthenticationFacade {
    public Authentication getAuthentication();
    
    @Component
    public class AuthenticationFacade implements IAuthenticationFacade {

        @Override
        public Authentication getAuthentication() {
            return SecurityContextHolder.getContext().getAuthentication();
        }
        
    }
}
