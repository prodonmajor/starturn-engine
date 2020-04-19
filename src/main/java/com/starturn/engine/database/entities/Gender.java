package com.starturn.engine.database.entities;
// Generated 01-Mar-2020 08:29:01 by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Gender generated by hbm2java
 */
@Entity
@Table(name="GENDER"
    ,catalog="starturn"
)
public class Gender  implements java.io.Serializable {


     private int id;
     private Long version;
     private String name;

    public Gender() {
    }

	
    public Gender(int id) {
        this.id = id;
    }
    public Gender(int id, String name) {
       this.id = id;
       this.name = name;
    }
   
     @Id 

    
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
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




}

