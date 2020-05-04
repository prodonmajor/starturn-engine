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
public class EsusuRepaymentScheduleDto {
    private int id;
    private Integer memberProfileId;
    private Integer esusuGroupId;
    private Integer esusuGroupMemberId;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private String repaymentDate;
    private String paidDate;
    private Boolean paid;
}
