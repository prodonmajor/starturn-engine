package com.starturn.engine.database.entities;
// Generated 01-Mar-2020 08:29:01 by Hibernate Tools 4.3.1


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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 * MemberProfile generated by hbm2java
 */
@Entity
@Table(name="MEMBER_PROFILE"
    ,catalog="starturn"
    , uniqueConstraints = {@UniqueConstraint(columnNames="email_address"), @UniqueConstraint(columnNames="username")} 
)
public class MemberProfile  implements java.io.Serializable {


     private Integer id;
     private Long version;
     private Bank bank;
     private String username;
     private String name;
     private String emailAddress;
     private String phoneNumber;
     private String gender;
     private String password;
     private Boolean accpetedTermsCondition;
     private Boolean locked;
     private Boolean firstTime;
     private Boolean active;
     private Boolean accountClosed;
     private Boolean accountSuspended;
     private Date accountSuspensionDate;
     private Date accountClosureDate;
     private Date creationDate;
     private Date lastLoginDate;
     private String lastLoginTime;
     private String bankAccountNumber;
     private String bvn;
     private String atmCardNo;
     private String atmCardExpiry;
     private Integer atmCvv;
     private Integer atmPin;
     private String atmCardType;
     private Date cardContributionDebitDate;
     private Set<UserToken> userTokens = new HashSet<UserToken>(0);
     private Set<EsusuGroupMembers> esusuGroupMemberses = new HashSet<EsusuGroupMembers>(0);
     private Set<Transaction> transactions = new HashSet<Transaction>(0);
     private Set<MemberWalletTransaction> memberWalletTransactions = new HashSet<MemberWalletTransaction>(0);
     private Set<TargetSavings> targetSavingses = new HashSet<TargetSavings>(0);
     private Set<UserGroup> userGroups = new HashSet<UserGroup>(0);
     private Set<EsusuGroupInvites> esusuGroupInviteses = new HashSet<EsusuGroupInvites>(0);
     private Date dob;

    public MemberProfile() {
    }

    public MemberProfile(Bank bank, String username, String name, String emailAddress, String phoneNumber, String gender, String password, Boolean accpetedTermsCondition, Boolean locked, Boolean firstTime, Boolean active, Boolean accountClosed, Boolean accountSuspended, Date accountSuspensionDate, Date accountClosureDate, Date creationDate, Date lastLoginDate, String lastLoginTime, String bankAccountNumber, String bvn, String atmCardNo, String atmCardExpiry, Integer atmCvv, Integer atmPin, String atmCardType, Date cardContributionDebitDate, Set<UserToken> userTokens, Set<EsusuGroupMembers> esusuGroupMemberses, Set<Transaction> transactions, Set<MemberWalletTransaction> memberWalletTransactions, Set<TargetSavings> targetSavingses, Set<UserGroup> userGroups, Set<EsusuGroupInvites> esusuGroupInviteses,Date dob) {
       this.bank = bank;
       this.username = username;
       this.name = name;
       this.emailAddress = emailAddress;
       this.phoneNumber = phoneNumber;
       this.gender = gender;
       this.password = password;
       this.accpetedTermsCondition = accpetedTermsCondition;
       this.locked = locked;
       this.firstTime = firstTime;
       this.active = active;
       this.accountClosed = accountClosed;
       this.accountSuspended = accountSuspended;
       this.accountSuspensionDate = accountSuspensionDate;
       this.accountClosureDate = accountClosureDate;
       this.creationDate = creationDate;
       this.lastLoginDate = lastLoginDate;
       this.lastLoginTime = lastLoginTime;
       this.bankAccountNumber = bankAccountNumber;
       this.bvn = bvn;
       this.atmCardNo = atmCardNo;
       this.atmCardExpiry = atmCardExpiry;
       this.atmCvv = atmCvv;
       this.atmPin = atmPin;
       this.atmCardType = atmCardType;
       this.cardContributionDebitDate = cardContributionDebitDate;
       this.userTokens = userTokens;
       this.esusuGroupMemberses = esusuGroupMemberses;
       this.transactions = transactions;
       this.memberWalletTransactions = memberWalletTransactions;
       this.targetSavingses = targetSavingses;
       this.userGroups = userGroups;
       this.esusuGroupInviteses = esusuGroupInviteses;
       this.dob = dob;
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
    @JoinColumn(name="bank_id")
    public Bank getBank() {
        return this.bank;
    }
    
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    
    @Column(name="username", unique=true, length=50)
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    
    @Column(name="name", length=500)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="email_address", unique=true, length=100)
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    
    @Column(name="phone_number", length=50)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
    @Column(name="gender", length=50)
    public String getGender() {
        return this.gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }

    
    @Column(name="password", length=500)
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    
    @Column(name="accpeted_terms_condition")
    public Boolean getAccpetedTermsCondition() {
        return this.accpetedTermsCondition;
    }
    
    public void setAccpetedTermsCondition(Boolean accpetedTermsCondition) {
        this.accpetedTermsCondition = accpetedTermsCondition;
    }

    
    @Column(name="locked")
    public Boolean getLocked() {
        return this.locked;
    }
    
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    
    @Column(name="first_time")
    public Boolean getFirstTime() {
        return this.firstTime;
    }
    
    public void setFirstTime(Boolean firstTime) {
        this.firstTime = firstTime;
    }

    
    @Column(name="active")
    public Boolean getActive() {
        return this.active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }

    
    @Column(name="account_closed")
    public Boolean getAccountClosed() {
        return this.accountClosed;
    }
    
    public void setAccountClosed(Boolean accountClosed) {
        this.accountClosed = accountClosed;
    }

    
    @Column(name="account_suspended")
    public Boolean getAccountSuspended() {
        return this.accountSuspended;
    }
    
    public void setAccountSuspended(Boolean accountSuspended) {
        this.accountSuspended = accountSuspended;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="account_suspension_date", length=10)
    public Date getAccountSuspensionDate() {
        return this.accountSuspensionDate;
    }
    
    public void setAccountSuspensionDate(Date accountSuspensionDate) {
        this.accountSuspensionDate = accountSuspensionDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="account_closure_date", length=10)
    public Date getAccountClosureDate() {
        return this.accountClosureDate;
    }
    
    public void setAccountClosureDate(Date accountClosureDate) {
        this.accountClosureDate = accountClosureDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="creation_date", length=10)
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="last_login_date", length=10)
    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }
    
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    
    @Column(name="last_login_time", length=50)
    public String getLastLoginTime() {
        return this.lastLoginTime;
    }
    
    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    
    @Column(name="bank_account_number", length=50)
    public String getBankAccountNumber() {
        return this.bankAccountNumber;
    }
    
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    
    @Column(name="bvn", length=500)
    public String getBvn() {
        return this.bvn;
    }
    
    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    
    @Column(name="atm_card_no", length=50)
    public String getAtmCardNo() {
        return this.atmCardNo;
    }
    
    public void setAtmCardNo(String atmCardNo) {
        this.atmCardNo = atmCardNo;
    }

    
    @Column(name="atm_card_expiry", length=50)
    public String getAtmCardExpiry() {
        return this.atmCardExpiry;
    }
    
    public void setAtmCardExpiry(String atmCardExpiry) {
        this.atmCardExpiry = atmCardExpiry;
    }

    
    @Column(name="atm_cvv")
    public Integer getAtmCvv() {
        return this.atmCvv;
    }
    
    public void setAtmCvv(Integer atmCvv) {
        this.atmCvv = atmCvv;
    }

    
    @Column(name="atm_pin")
    public Integer getAtmPin() {
        return this.atmPin;
    }
    
    public void setAtmPin(Integer atmPin) {
        this.atmPin = atmPin;
    }

    
    @Column(name="atm_card_type", length=50)
    public String getAtmCardType() {
        return this.atmCardType;
    }
    
    public void setAtmCardType(String atmCardType) {
        this.atmCardType = atmCardType;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="card_contribution_debit_date", length=10)
    public Date getCardContributionDebitDate() {
        return this.cardContributionDebitDate;
    }
    
    public void setCardContributionDebitDate(Date cardContributionDebitDate) {
        this.cardContributionDebitDate = cardContributionDebitDate;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="memberProfile")
    public Set<UserToken> getUserTokens() {
        return this.userTokens;
    }
    
    public void setUserTokens(Set<UserToken> userTokens) {
        this.userTokens = userTokens;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="memberProfile")
    public Set<EsusuGroupMembers> getEsusuGroupMemberses() {
        return this.esusuGroupMemberses;
    }
    
    public void setEsusuGroupMemberses(Set<EsusuGroupMembers> esusuGroupMemberses) {
        this.esusuGroupMemberses = esusuGroupMemberses;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="memberProfile")
    public Set<Transaction> getTransactions() {
        return this.transactions;
    }
    
    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="memberProfile")
    public Set<MemberWalletTransaction> getMemberWalletTransactions() {
        return this.memberWalletTransactions;
    }
    
    public void setMemberWalletTransactions(Set<MemberWalletTransaction> memberWalletTransactions) {
        this.memberWalletTransactions = memberWalletTransactions;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="memberProfile")
    public Set<TargetSavings> getTargetSavingses() {
        return this.targetSavingses;
    }
    
    public void setTargetSavingses(Set<TargetSavings> targetSavingses) {
        this.targetSavingses = targetSavingses;
    }

@ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="user_group_members", catalog="starturn", joinColumns = { 
        @JoinColumn(name="member_profile_id", nullable=false, updatable=false) }, inverseJoinColumns = { 
        @JoinColumn(name="user_group_id", nullable=false, updatable=false) })
    public Set<UserGroup> getUserGroups() {
        return this.userGroups;
    }
    
    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="memberProfile")
    public Set<EsusuGroupInvites> getEsusuGroupInviteses() {
        return this.esusuGroupInviteses;
    }
    
    public void setEsusuGroupInviteses(Set<EsusuGroupInvites> esusuGroupInviteses) {
        this.esusuGroupInviteses = esusuGroupInviteses;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="dob", length=10)
    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }




}


