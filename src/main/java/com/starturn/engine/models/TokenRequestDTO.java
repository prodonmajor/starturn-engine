/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author IDOKO EMMANUEL
 */
@Getter
@Setter
@ToString
public class TokenRequestDTO {
private String phoneNumber;  
private String emailAddress;
private String name;
}
