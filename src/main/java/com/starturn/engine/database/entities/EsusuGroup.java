package com.starturn.engine.database.entities;
// Generated 01-Mar-2020 08:29:01 by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * EsusuGroup generated by hbm2java
 */
@Entity
@Table(name="ESUSU_GROUP"
    ,catalog="starturn"
)
public class EsusuGroup  implements java.io.Serializable {


     private Integer id;
     private Long version;
     private ContributionFrequency contributionFrequency;
     private String name;
     private String description;
     private String code;
     private BigDecimal contributionAmount;
     private Integer numberOfContributors;
     private BigDecimal minAmountLockedInMemberAccount;
     private Date startDate;
     private Date endDate;
     private Date creationDate;
     private String createdByUsername;
     private Boolean circleCompleted;
     private BigDecimal monthlyCollectionAmount;
     private Set<EsusuGroupMembers> esusuGroupMemberses = new HashSet<EsusuGroupMembers>(0);
     private Set<Transaction> transactions = new HashSet<Transaction>(0);
     private Set<EsusuGroupInvites> esusuGroupInviteses = new HashSet<EsusuGroupInvites>(0);

    public EsusuGroup() {
    }

    public EsusuGroup(ContributionFrequency contributionFrequency, String name, String description, String code, BigDecimal contributionAmount, Integer numberOfContributors, BigDecimal minAmountLockedInMemberAccount, Date startDate, Date endDate, Date creationDate, String createdByUsername, Boolean circleCompleted, BigDecimal monthlyCollectionAmount, Set<EsusuGroupMembers> esusuGroupMemberses, Set<Transaction> transactions, Set<EsusuGroupInvites> esusuGroupInviteses) {
       this.contributionFrequency = contributionFrequency;
       this.name = name;
       this.description = description;
       this.code = code;
       this.contributionAmount = contributionAmount;
       this.numberOfContributors = numberOfContributors;
       this.minAmountLockedInMemberAccount = minAmountLockedInMemberAccount;
       this.startDate = startDate;
       this.endDate = endDate;
       this.creationDate = creationDate;
       this.createdByUsername = createdByUsername;
       this.circleCompleted = circleCompleted;
       this.monthlyCollectionAmount = monthlyCollectionAmount;
       this.esusuGroupMemberses = esusuGroupMemberses;
       this.transactions = transactions;
       this.esusuGroupInviteses = esusuGroupInviteses;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    @Column(name="version")
    public Long getVersion() {
        return this.version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="contribution_frequency_id")
    public ContributionFrequency getContributionFrequency() {
        return this.contributionFrequency;
    }
    
    public void setContributionFrequency(ContributionFrequency contributionFrequency) {
        this.contributionFrequency = contributionFrequency;
    }

    
    @Column(name="name", length=500)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="description", length=500)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    
    @Column(name="code", length=50)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    
    @Column(name="contribution_amount", scale=4)
    public BigDecimal getContributionAmount() {
        return this.contributionAmount;
    }
    
    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    
    @Column(name="number_of_contributors")
    public Integer getNumberOfContributors() {
        return this.numberOfContributors;
    }
    
    public void setNumberOfContributors(Integer numberOfContributors) {
        this.numberOfContributors = numberOfContributors;
    }

    
    @Column(name="min_amount_locked_in_member_account", scale=4)
    public BigDecimal getMinAmountLockedInMemberAccount() {
        return this.minAmountLockedInMemberAccount;
    }
    
    public void setMinAmountLockedInMemberAccount(BigDecimal minAmountLockedInMemberAccount) {
        this.minAmountLockedInMemberAccount = minAmountLockedInMemberAccount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="start_date", length=10)
    public Date getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="end_date", length=10)
    public Date getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="creation_date", length=10)
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    
    @Column(name="created_by_username", length=50)
    public String getCreatedByUsername() {
        return this.createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    
    @Column(name="circle_completed")
    public Boolean getCircleCompleted() {
        return this.circleCompleted;
    }
    
    public void setCircleCompleted(Boolean circleCompleted) {
        this.circleCompleted = circleCompleted;
    }

    
    @Column(name="monthly_collection_amount", scale=4)
    public BigDecimal getMonthlyCollectionAmount() {
        return this.monthlyCollectionAmount;
    }
    
    public void setMonthlyCollectionAmount(BigDecimal monthlyCollectionAmount) {
        this.monthlyCollectionAmount = monthlyCollectionAmount;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="esusuGroup")
    public Set<EsusuGroupMembers> getEsusuGroupMemberses() {
        return this.esusuGroupMemberses;
    }
    
    public void setEsusuGroupMemberses(Set<EsusuGroupMembers> esusuGroupMemberses) {
        this.esusuGroupMemberses = esusuGroupMemberses;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="esusuGroup")
    public Set<Transaction> getTransactions() {
        return this.transactions;
    }
    
    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="esusuGroup")
    public Set<EsusuGroupInvites> getEsusuGroupInviteses() {
        return this.esusuGroupInviteses;
    }
    
    public void setEsusuGroupInviteses(Set<EsusuGroupInvites> esusuGroupInviteses) {
        this.esusuGroupInviteses = esusuGroupInviteses;
    }




}

