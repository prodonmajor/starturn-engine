/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import java.math.BigDecimal;
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
public class EsusuGroupDTO {

    @Min(0)
    @NotNull(message = "id cannot be null")
    private Integer id;
    private Integer contributionFrequencyId;
    @NotBlank(message = "username must be provided")
    private String name;
    @NotNull(message = "locked cannot be null")
    private String description;
    @NotNull(message = "locked cannot be null")
    private String code;
    @NotNull(message = "locked cannot be null")
    private BigDecimal contributionAmount;
    @Min(2)
    @NotNull(message = "number of contributors cannot be null")
    private Integer numberOfContributors;
    @NotNull(message = "Minimum amount locked in member account cannot be null")
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
}
