package com.starturn.engine.database.entities;
// Generated 01-Mar-2020 08:29:01 by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * RequirementGroupAccess generated by hbm2java
 */
@Entity
@Table(name="REQUIREMENT_GROUP_ACCESS"
    ,catalog="starturn"
)
public class RequirementGroupAccess  implements java.io.Serializable {


     private Integer id;
     private Long version;
     private Requirement requirement;
     private RequirementGroup requirementGroup;

    public RequirementGroupAccess() {
    }

    public RequirementGroupAccess(Requirement requirement, RequirementGroup requirementGroup) {
       this.requirement = requirement;
       this.requirementGroup = requirementGroup;
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
    @JoinColumn(name="requirement_id", nullable=false)
    public Requirement getRequirement() {
        return this.requirement;
    }
    
    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="requirement_group_id", nullable=false)
    public RequirementGroup getRequirementGroup() {
        return this.requirementGroup;
    }
    
    public void setRequirementGroup(RequirementGroup requirementGroup) {
        this.requirementGroup = requirementGroup;
    }




}

