/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import java.math.BigDecimal;
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
public class MemberWalletDto {
private Integer id;
private String memberName; 
private BigDecimal balance;
private Integer memberProfileId;

}
