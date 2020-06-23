/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.entities;

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
 *
 * @author Administrator
 */
@Entity
@Table(name = "MEMBER_PROFILE_PICTURE",catalog = "starturn"
)
public class MemberProfilePicture implements java.io.Serializable {

    private int id;
    private Long version;
    private MemberProfile memberProfile;
    private String picturepath;

    public MemberProfilePicture() {
    }

    public MemberProfilePicture(int id, Long version, MemberProfile memberProfile, String picturePath) {
        this.id = id;
        this.version = version;
        this.memberProfile = memberProfile;
        this.picturepath = picturePath;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_profile_id")
    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    @Column(name = "picture_path", length = 500)
    public String getPicturepath() {
        return picturepath;
    }

    public void setPicturepath(String picturepath) {
        this.picturepath = picturepath;
    }

}
