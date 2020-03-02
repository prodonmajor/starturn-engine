/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller.businesslogic.async;

import com.starturn.database.entities.MemberProfile;
import com.starturn.database.query.DaoServiceQuery;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
public class AsyncRunner {
    private @Autowired DaoServiceQuery daoService;
    
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void processMemberLastLogin(Integer memberProfileId) throws Exception {
        MemberProfile memberProfile = (MemberProfile) daoService.getEntity(MemberProfile.class, memberProfileId);
        memberProfile.setLastLoginDate(new Date());
        
        daoService.saveUpdateEntity(memberProfile);
    }
}
