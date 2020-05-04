/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.entities;

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
 *
 * @author IDOKO EMMANUEL
 */
@Entity
@Table(name = "INTEREST_DISBURSEMENT_TYPE",
        catalog = "starturn"
)
public class InterestDisbursementType implements java.io.Serializable {

    private Integer id;
    private Long version;
    private String name;
    private Set<EsusuGroup> esusuGroups = new HashSet<EsusuGroup>(0);

    public InterestDisbursementType() {
    }

    public InterestDisbursementType(String name) {
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

    @Column(name = "name", length = 500)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "interestDisbursementType")
    public Set<EsusuGroup> getEsusuGroups() {
        return esusuGroups;
    }

    public void setEsusuGroups(Set<EsusuGroup> esusuGroups) {
        this.esusuGroups = esusuGroups;
    }
    
    
}
