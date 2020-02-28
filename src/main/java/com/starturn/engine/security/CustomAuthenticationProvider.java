/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.security;

import com.starturn.database.entities.MemberProfile;
import com.starturn.database.query.MemberServiceQuery;
import com.starturn.engine.models.response.ErrorMessage;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author akinw
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LogManager.getLogger(CustomAuthenticationProvider.class);
    
    private @Autowired ErrorMessage errorMessage;
    private @Autowired PasswordEncoder passwordEncoder;
    private @Autowired MemberServiceQuery credentials;
    private @Autowired MemberProfile memberProfile;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginGeniuneFailMessage = "";
        boolean loginGeniuneFail = false;
        try {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            
            if (!credentials.checkUserExists(username)) {
                logger.info("{} is an invalid user", username);
                errorMessage.setMessage("Username or password incorrect. Please, try again");
                
                loginGeniuneFail = true;
                loginGeniuneFailMessage = username + " is an invalid user";
                throw new UsernameNotFoundException(loginGeniuneFailMessage);
            }
            
            memberProfile = credentials.getUserInformation(username);
            
            if (passwordEncoder.matches(password, memberProfile.getPassword())) {
                logger.info("User, {}, checks out", username);
                
                //check if account is closed
                if (memberProfile.getAccountClosed()) {
                    logger.info("The account, {}, has been closed", username);
                    errorMessage.setMessage("The account, " + username + ", has been closed");
                    
                    loginGeniuneFail = true;
                    loginGeniuneFailMessage = "The account, " + username + ", has been closed";
                    throw new UsernameNotFoundException(loginGeniuneFailMessage);
                }
                
                //check if account has been suspended
                if (memberProfile.getAccountSuspended()) {
                    logger.info("The account, {}, has been suspended", username);
                    errorMessage.setMessage("The account, " + username + ", has been suspended");
                    
                    loginGeniuneFail = true;
                    loginGeniuneFailMessage = "The account, " + username + ", has been suspended";
                    throw new UsernameNotFoundException(loginGeniuneFailMessage);
                }
                
                //check if account has been locked
                if (memberProfile.getLocked()) {
                    logger.info("The account, {}, has been locked", username);
                    errorMessage.setMessage("The account, " + username + ", has been locked");
                    
                    loginGeniuneFail = true;
                    loginGeniuneFailMessage = "The account, " + username + ", has been locked";
                    throw new UsernameNotFoundException(loginGeniuneFailMessage);
                }
                
                return new UsernamePasswordAuthenticationToken(username, password, getGrantedAuthorities());
            } else {
                logger.info("User, {}, does not check out", username);
                errorMessage.setMessage("Username or password incorrect. Please, try again");
                
                loginGeniuneFail = true;
                loginGeniuneFailMessage = username + "'s password is incorrect";
                throw new BadCredentialsException(loginGeniuneFailMessage);
            }
        } catch (Exception ex) {
            if (loginGeniuneFail) {
                logger.info("{}", loginGeniuneFailMessage);
                throw new UsernameNotFoundException(loginGeniuneFailMessage);
            } else {
                logger.info("Internal error thrown during authentication process");
                logger.error("Internal error thrown during authentication process", ex);
                errorMessage.setMessage("Internal error thrown during authentication process. Please, try again later");
                throw new BadCredentialsException("Internal error thrown during authentication process", ex);
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
    private List<GrantedAuthority> getGrantedAuthorities(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.clear();        
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
       
        return authorities;
    }
    
}
