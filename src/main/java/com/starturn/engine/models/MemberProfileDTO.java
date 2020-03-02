/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import javax.validation.constraints.Min;
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
public class MemberProfileDTO {

    //@Min(0)
    @NotNull(message = "id cannot be null")
    private Integer id;
    @NotBlank(message = "username must be provided")
    private String username;
    @NotBlank(message = "name must be provided")
    private String name;
    @NotBlank(message = "email must be provided")
    private String emailAddress;
    @NotBlank(message = "phone number must be provided")
    private String phoneNumber;
    private String gender;
    private String password;
    @NotNull(message = "accepted terms and conditions must be specified")
    private Boolean accpetedTermsCondition;
    @NotNull(message = "locked cannot be null")
    private Boolean locked;
    @NotNull(message = "first time cannot be null")
    private Boolean firstTime;
    @NotNull(message = "active cannot be null")
    private Boolean active;
    @NotNull(message = "account closed cannot be null")
    private Boolean accountClosed;
    @NotNull(message = "account suspended cannot be null")
    private Boolean accountSuspended;
    private String accountSuspensionDate;
    private String accountClosureDate;
    private String creationDate;
    private String lastLoginDate;
    private String lastLoginTime;
    
}
