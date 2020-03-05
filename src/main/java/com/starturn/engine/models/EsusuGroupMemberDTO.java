/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import java.math.BigDecimal;
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
public class EsusuGroupMemberDTO {

    @NotNull(message = "id cannot be null")
    private Integer id;
    @NotNull(message = "esusu group id cannot be null")
    private Integer esusuGroupId;
    @NotNull(message = "member profile id cannot be null")
    private Integer memberProfileId;
    @NotNull(message = "collection position must be a number")
    private Integer collectionPosition;
    @NotNull(message = "expected amount cannot be null")
    private BigDecimal expectedAmount;
    @NotNull(message = "amount paid cannot be null")
    private BigDecimal amountPaid;
    @NotNull(message = "paid flag cannot be null")
    private Boolean paid;
    @NotNull(message = "paid date cannot be null")
    private String paidDate;
    @NotNull(message = "creation date cannot be null")
    private String creationDate;
    @NotNull(message = "expected collection date must be specified")
    private String expectedCollectionDate;
    private String createdByUsername;
}
