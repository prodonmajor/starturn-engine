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
public class TransactionDTO {

    private int id;
    private int esusuGroupId;    
    private String esusuGroupName;
    private int memberProfileId;    
    private String memberName;
    private String username;
    private int transactionTypeId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal amountInAndOut;
    private boolean isEsusuContribution;
    private boolean isAutoDebit;
    private boolean isCardPayment;
    private boolean isTargetSavings;
    private String transactionDate;
}
