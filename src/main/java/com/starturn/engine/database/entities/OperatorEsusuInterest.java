/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author IDOKO EMMANUEL
 */
@Entity
@Table(name="OPERATOR_ESUSU_INTEREST"
    ,catalog="starturn"
)
public class OperatorEsusuInterest implements java.io.Serializable {
  private Integer id;
  private Long version;  
  private EsusuGroup esusuGroup;
  private Date collectionDate;
  private BigDecimal interestAmount;
  private BigDecimal interestAmountForOperator;

    public OperatorEsusuInterest() {
    }

    public OperatorEsusuInterest(EsusuGroup esusuGroup, Date collectionDate, BigDecimal interestAmount, BigDecimal interestAmountForOperator) {
        this.esusuGroup = esusuGroup;
        this.collectionDate = collectionDate;
        this.interestAmount = interestAmount;
        this.interestAmountForOperator = interestAmountForOperator;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esusu_group_id")
    public EsusuGroup getEsusuGroup() {
        return esusuGroup;
    }

    public void setEsusuGroup(EsusuGroup esusuGroup) {
        this.esusuGroup = esusuGroup;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "collection_date", length = 10)
    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    @Column(name = "interest_amount", scale = 4)
    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    @Column(name = "interest_amount_for_operator", scale = 4)
    public BigDecimal getInterestAmountForOperator() {
        return interestAmountForOperator;
    }

    public void setInterestAmountForOperator(BigDecimal interestAmountForOperator) {
        this.interestAmountForOperator = interestAmountForOperator;
    }
  
  
}
