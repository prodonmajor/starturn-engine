/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import com.starturn.database.entities.EsusuGroup;
import com.starturn.database.entities.MemberProfile;
import java.math.BigDecimal;
import java.util.Date;
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
public class EsusuGroupInvitesDTO {

    @Min(0)
    @NotNull(message = "id cannot be null")
    private int id;
    @NotNull(message = "id cannot be null")
    private Integer esusuGroupId;
    @NotNull(message = "id cannot be null")
    private Integer memberProfileId;
    @NotNull(message = "id cannot be null")
    private String emailAddress;
    @NotNull(message = "id cannot be null")
    private String phoneNumber;
    private BigDecimal expectedAmount;
    private Boolean accepted;
    private Boolean rejected;
    private Date responseDate;
    private Date invitedDate;
    private String invitedByUsername;
}
