/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.models.response;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author akinw
 */

@Getter
@Setter
@ToString
public class ResponseInformation {
    private Date timestamp;
    private String errorcode;
    private String message;
    private String otherDetails;

    public ResponseInformation() {
    }

    public ResponseInformation(String message) {
        this.message = message;
    }

    public ResponseInformation(Date timestamp, String message, String otherDetails) {
        this.timestamp = timestamp;
        this.message = message;
        this.otherDetails = otherDetails;
    }
}
