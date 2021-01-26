/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class MemberContributionCardPaymentDTO {

    //@NotNull(message = "esusu group id cannot be null")
    private int esusuGroupId;
    //@NotNull(message = "member id cannot be null")
    private int memberProfileId;
    //@NotNull(message = "amount cannot be null")
    private BigDecimal amount;
    //@NotBlank(message = "Transaction reference must be provided")
    private String transactionReference;
}
