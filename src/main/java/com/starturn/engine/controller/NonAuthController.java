/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller;

import com.starturn.engine.controller.businesslogic.RequestLogic;
import com.starturn.engine.models.MemberProfileDTO;
import com.starturn.engine.models.response.ResponseInformation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/starturn/api/v1/nonauth")
@Api(value = "Request API calls")
public class NonAuthController {

    private @Autowired
    RequestLogic logic;

    @PostMapping("/signup")
    @ApiOperation(value = "New member registration / sign up")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberProfileDTO dto, BindingResult result) throws Exception {
        return logic.signUp(dto, result);
    }

    @PostMapping("/validatetoken")
    @ApiOperation(value = "Token validation")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> validateToken(@RequestParam(value = "token", required = true) String token) throws Exception {
        return logic.validateToken(token);
    }

    @GetMapping("/generatetoken")
    @ApiOperation(value = "Token generation")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> generateToken(@RequestParam(value = "email", required = true) String emailAddress) throws Exception {
        return logic.generateToken(emailAddress);
    }
}
