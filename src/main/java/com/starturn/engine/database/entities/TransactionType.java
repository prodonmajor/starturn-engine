package com.starturn.engine.database.entities;
// Generated 01-Mar-2020 08:29:01 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * TransactionType generated by hbm2java
 */
@Entity
@Table(name="TRANSACTION_TYPE"
    ,catalog="starturn"
)
public class TransactionType  implements java.io.Serializable {


     private Integer id;
     private Long version;
     private String name;
     private Set<MemberWalletTransaction> memberWalletTransactions = new HashSet<MemberWalletTransaction>(0);
     private Set<Transaction> transactions = new HashSet<Transaction>(0);

    public TransactionType() {
    }

    public TransactionType(String name, Set<MemberWalletTransaction> memberWalletTransactions, Set<Transaction> transactions) {
       this.name = name;
       this.memberWalletTransactions = memberWalletTransactions;
       this.transactions = transactions;
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

    
    @Column(name="name", length=500)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="transactionType")
    public Set<MemberWalletTransaction> getMemberWalletTransactions() {
        return this.memberWalletTransactions;
    }
    
    public void setMemberWalletTransactions(Set<MemberWalletTransaction> memberWalletTransactions) {
        this.memberWalletTransactions = memberWalletTransactions;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="transactionType")
    public Set<Transaction> getTransactions() {
        return this.transactions;
    }
    
    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }




}


