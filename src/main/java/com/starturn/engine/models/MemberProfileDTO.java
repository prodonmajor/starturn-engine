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
public class MemberProfileDTO {

    private Integer id;
    //@NotBlank(message = "username is required")
    private String username;
    //@NotBlank(message = "name is required")
    private String name;
    //@NotBlank(message = "email is required")
    private String emailAddress;
    //@NotBlank(message = "phone number is required")
    private String phoneNumber;
    private String gender;
    private String password;
    private String bvn;
    private Boolean accpetedTermsCondition;
    private Boolean locked;
    private Boolean firstTime;
    private Boolean active;
    private Boolean accountClosed;
    private Boolean accountSuspended;
    private String accountSuspensionDate;
    private String accountClosureDate;
    private String creationDate;
    private String lastLoginDate;
    private String lastLoginTime;
    //@NotNull(message = "date of birth cannot be null")
    private String dob;
    private String token;
    
}
