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
@Table(name = "MEMBER_WALLET",
        catalog = "starturn"
)
public class MemberWallet implements java.io.Serializable {

    private Integer id;
    private Long version;
    private MemberProfile memberProfile;
    private BigDecimal balance;
    private Date creatioDate;
    private Boolean active;

    public MemberWallet() {
    }

    public MemberWallet(MemberProfile memberProfile, BigDecimal balance, Date creatioDate, Boolean active) {
        this.memberProfile = memberProfile;
        this.balance = balance;
        this.creatioDate = creatioDate;
        this.active = active;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Column(name = "balance", scale = 4)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "creation_date", length = 10)
    public Date getCreatioDate() {
        return creatioDate;
    }

    public void setCreatioDate(Date creatioDate) {
        this.creatioDate = creatioDate;
    }

    @Column(name="active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
