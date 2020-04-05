/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.config;

import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.query.DaoServiceQuery;
import com.starturn.engine.database.query.MemberServiceQuery;
import com.starturn.engine.database.query.factory.ServiceQueryFactory;
import com.starturn.engine.models.response.ErrorMessage;
import com.starturn.engine.util.BytesConverter;
import com.starturn.engine.util.DateUtility;
import com.starturn.engine.util.GeneralUtility;
import com.starturn.engine.util.modelmapping.ModelMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Administrator
 */
@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapping getModelMapping() {
        return new ModelMapping();
    }

    @Bean
    public DateUtility getDateUtility() {
        return new DateUtility();
    }

    @Bean
    public GeneralUtility getGeneralUtility() {
        return new GeneralUtility();
    }

    @Bean
    public BytesConverter getBytesConverter() {
        return new BytesConverter();
    }

    @Bean
    public ErrorMessage getErrorMessage() {
        return new ErrorMessage();
    }

    @Bean
    public MemberServiceQuery getMemberServiceQuery() {
        return ServiceQueryFactory.getMemberServiceQuery();
    }
//    
    @Bean
    public DaoServiceQuery getDaoServiceQuery() {
        return ServiceQueryFactory.getDaoServiceQuery();
    }
//    
    @Bean
    public MemberProfile getMemberProfile() {
        return new MemberProfile();
    }

   
//    @Bean
//    public EmailAlertHelper getEmailAlertHelper() {
//        return new EmailAlertHelper();
//    }
//    
//    @Bean
//    public SmsAlertHelper getSmsAlertHelper() {
//        return new SmsAlertHelper();
//    }
}
