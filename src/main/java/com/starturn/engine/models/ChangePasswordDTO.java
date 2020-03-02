/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
@ToString
public class ChangePasswordDTO {
    @NotBlank(message = "old password must be provided")
    private String oldPassword;
    @NotBlank(message = "password must be provided")
    private String password;
    @NotBlank(message = "confirmation password must be provided")
    private String confirmPassword;
    private Boolean isfirstTime;
    @NotNull(message = "token must be provided")
    private String token;
}
