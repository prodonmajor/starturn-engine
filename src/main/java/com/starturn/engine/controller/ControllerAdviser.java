/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller;

import com.starturn.engine.models.response.ResponseInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author IDOKO EMMANUEL
 */
@ControllerAdvice
public class ControllerAdviser {
    private static final Logger logger = LogManager.getLogger(ControllerAdviser.class);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception exception) {
        logger.error("An unusual error occured:", exception);
        
        ResponseInformation errorResp = new ResponseInformation();
        errorResp.setErrorcode("500");
        errorResp.setMessage(exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResp);
    }
}
