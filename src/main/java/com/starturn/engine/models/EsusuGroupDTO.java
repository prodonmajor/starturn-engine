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
 * @author Administrator
 */
@Getter
@Setter
@ToString
public class EsusuGroupDTO {

    
    @NotNull(message = "id cannot be null")
    private Integer id;
    private Integer contributionFrequencyId;
    private String contributionFrequencyName;
    @NotBlank(message = "username must be provided")
    private String name;
    @NotNull(message = "description cannot be null")
    private String description;
    private String code;
    @NotNull(message = "contribution amount cannot be null")
    private BigDecimal contributionAmount;
    @NotNull(message = "number of contributors cannot be null")
    private Integer numberOfContributors;
    private BigDecimal minAmountLockedInMemberAccount;
    @NotNull(message = "start date cannot be null")
    private String startDate;
    @NotNull(message = "end date cannot be null")
    private String endDate;
    private String creationDate;
    @NotBlank(message = "created by username cannot be null")
    private String createdByUsername;
    @NotNull(message = "circle ended flag cannot be null")
    private Boolean circleEnded;
    @NotNull(message = "interest disbursement type id cannot be null")
    private Integer interest_disbursement_type_id;
    private String interestDisbursementTypeName;
    private Boolean positionArranged;
}
