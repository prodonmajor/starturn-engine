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
@Table(name = "ESUSU_REPAYMENT_SCHEDULE",
         catalog = "starturn"
)
public class EsusuRepaymentSchedule implements java.io.Serializable {

    private int id;
    private Long version;
    private MemberProfile memberProfile;
    private EsusuGroup esusuGroup;
    private EsusuGroupMembers esusuGroupMembers;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private Date repaymentDate;
    private Date paidDate;
    private Boolean paid;

    public EsusuRepaymentSchedule() {
    }

    public EsusuRepaymentSchedule(MemberProfile memberProfile, EsusuGroup esusuGroup, EsusuGroupMembers esusuGroupMembers, BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal totalAmount, Date repaymentDate, Date paidDate, Boolean paid) {
        this.memberProfile = memberProfile;
        this.esusuGroup = esusuGroup;
        this.esusuGroupMembers = esusuGroupMembers;
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.totalAmount = totalAmount;
        this.repaymentDate = repaymentDate;
        this.paidDate = paidDate;
        this.paid = paid;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Version
    @Column(name = "version")
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id")
    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esusu_group_id")
    public EsusuGroup getEsusuGroup() {
        return esusuGroup;
    }

    public void setEsusuGroup(EsusuGroup esusuGroup) {
        this.esusuGroup = esusuGroup;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esusu_group_member_id")
    public EsusuGroupMembers getEsusuGroupMembers() {
        return esusuGroupMembers;
    }

    public void setEsusuGroupMembers(EsusuGroupMembers esusuGroupMembers) {
        this.esusuGroupMembers = esusuGroupMembers;
    }

    @Column(name = "principal_amount", scale = 4)
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    @Column(name = "interest_amount", scale = 4)
    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    @Column(name = "total_amount", scale = 4)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "payment_date", length = 10)
    public Date getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(Date repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "paid_date", length = 10)
    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    @Column(name = "paid")
    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

}
