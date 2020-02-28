/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.security.entrypoint;

import com.starturn.engine.models.response.ErrorMessage;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 *
 * @author Administrator
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private @Autowired ErrorMessage errorMessage;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException, ServletException {
        String message;
        if (errorMessage.getMessage() != null && !errorMessage.getMessage().trim().isEmpty()) {
            message = errorMessage.getMessage();
        } else {
            message = ex.getMessage();
        }
        
        String json = String.format("{\"errorcode\": \"%s\", \"message\": \"%s\"}", response.getStatus(), message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}

