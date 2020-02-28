/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models;

import java.util.List;
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
public class EsusuGroupInviteWrapper {

    @Min(1)
    @NotNull(message = "esusu group id cannot be null")
    private Integer groupId;
    @NotNull(message = "usernames of users to invite must not be null")
    List<String> usernames;
}
