/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import javax.validation.constraints.Min;
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
public class CardDetailsDTO {

    @Min(1)
    @NotNull(message = "bank id cannot be null")
    private Integer bankId;
    @NotNull(message = "bank account number cannot be null")
    private String bankAccountNumber;
    @NotNull(message = "BVN cannot be null")
    private String bvn;
    @NotNull(message = "card debit date cannot be null")
    private String cardContributionDebitDate;
}
