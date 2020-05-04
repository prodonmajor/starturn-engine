/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author IDOKO EMMANUEL
 */
@Entity
@Table(name = "PAYMENT_TYPE",
         catalog = "starturn"
)
public class PaymentType implements java.io.Serializable {

    private Integer id;
    private Long version;
    private String name;

    public PaymentType() {
    }

    public PaymentType(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    @Column(name = "version")
    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name="name", length=500)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
