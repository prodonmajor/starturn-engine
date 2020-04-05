/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.modelmapping;

import com.starturn.engine.database.entities.ContributionFrequency;
import com.starturn.engine.database.entities.EsusuGroup;
import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.query.factory.ServiceQueryFactory;
import com.starturn.engine.models.EsusuGroupDTO;
import com.starturn.engine.models.MemberProfileDTO;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ModelMapping {

    private static final Logger logger = LogManager.getLogger(ModelMapping.class);

    public MemberProfileDTO memberToDtoMapping(MemberProfile memberProfile) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        MemberProfileDTO dto = new MemberProfileDTO();

        if (memberProfile != null) {

            dto.setId(memberProfile.getId());
            dto.setUsername(memberProfile.getUsername());
            dto.setName(memberProfile.getName());
            dto.setEmailAddress(memberProfile.getEmailAddress());
            dto.setPhoneNumber(memberProfile.getPhoneNumber());
            dto.setAccountClosed(memberProfile.getAccountClosed());
            dto.setAccountSuspended(memberProfile.getAccountSuspended());
            dto.setLocked(memberProfile.getLocked());
            dto.setAccpetedTermsCondition(memberProfile.getAccpetedTermsCondition());
            dto.setCreationDate(formatter.format(memberProfile.getCreationDate()));
            dto.setGender(memberProfile.getGender());
            if (memberProfile.getLastLoginDate() != null) {
                dto.setLastLoginDate(formatter.format(memberProfile.getLastLoginDate()));
            }

            dto.setActive(memberProfile.getActive());

        }

        return dto;
    }

    public MemberProfile dtoToMember(MemberProfileDTO dto) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        MemberProfile memberProfile = new MemberProfile();

        if (dto != null) {

            if (dto.getId() != null && dto.getId() > 0 && ServiceQueryFactory.getDaoServiceQuery().
                    checkObjectExists(MemberProfile.class, dto.getId())) {
                memberProfile = (MemberProfile) ServiceQueryFactory.getDaoServiceQuery().
                        getEntity(MemberProfile.class, dto.getId());
            }

            memberProfile.setUsername(dto.getUsername());
            memberProfile.setName(dto.getName());
            memberProfile.setEmailAddress(dto.getEmailAddress());
            memberProfile.setPhoneNumber(dto.getPhoneNumber());
            memberProfile.setAccountClosed(dto.getAccountClosed());
            memberProfile.setAccountSuspended(dto.getAccountSuspended());
            memberProfile.setLocked(dto.getLocked());
            memberProfile.setAccpetedTermsCondition(dto.getAccpetedTermsCondition());
            memberProfile.setCreationDate(!"".equals(dto.getCreationDate()) ? formatter.parse(dto.getCreationDate()) : new Date());
            memberProfile.setGender(dto.getGender());
            memberProfile.setActive(dto.getActive() != null ? dto.getActive() : false);
        }

        return memberProfile;
    }

    public EsusuGroupDTO EsusuGroupToDto(EsusuGroup esusuGroup) {
        EsusuGroupDTO dto = new EsusuGroupDTO();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (esusuGroup != null) {
                dto.setId(esusuGroup.getId());
                dto.setCode(esusuGroup.getCode());
                dto.setContributionAmount(esusuGroup.getContributionAmount());
                dto.setCircleEnded(false);
                dto.setContributionFrequencyId(esusuGroup.getContributionFrequency().getId());
                dto.setCreatedByUsername(esusuGroup.getCreatedByUsername());
                dto.setCreationDate(formatter.format(esusuGroup.getCreationDate()));
                dto.setDescription(esusuGroup.getDescription());
                dto.setEndDate(formatter.format(esusuGroup.getEndDate()));
                dto.setMinAmountLockedInMemberAccount(esusuGroup.getMinAmountLockedInMemberAccount());
                dto.setName(esusuGroup.getName());
                dto.setNumberOfContributors(esusuGroup.getNumberOfContributors());
                dto.setStartDate(formatter.format(esusuGroup.getStartDate()));
            }
        } catch (Exception ex) {
            logger.error("An error occured while converting from esusu group entity to esusu group dto. ", ex);
        }
        return dto;
    }

    public EsusuGroup dtoToEsusuGroup(EsusuGroupDTO dto) {
        EsusuGroup esusuGroup = new EsusuGroup();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (dto != null) {
                if (dto.getId() > 0 && ServiceQueryFactory.getDaoServiceQuery().
                        checkObjectExists(EsusuGroup.class, dto.getId())) {
                    esusuGroup = (EsusuGroup) ServiceQueryFactory.getDaoServiceQuery().getEntity(EsusuGroup.class, dto.getId());
                }
                if (dto.getContributionFrequencyId() > 0 && ServiceQueryFactory.getDaoServiceQuery().
                        checkObjectExists(ContributionFrequency.class, dto.getContributionFrequencyId())) {
                    ContributionFrequency frequency = (ContributionFrequency) ServiceQueryFactory.getDaoServiceQuery().getEntity(ContributionFrequency.class, dto.getContributionFrequencyId());
                    esusuGroup.setContributionFrequency(frequency);
                }

                esusuGroup.setCode(dto.getCode());
                esusuGroup.setContributionAmount(dto.getContributionAmount());
                esusuGroup.setMonthlyCollectionAmount(dto.getContributionAmount().multiply(new BigDecimal(dto.getNumberOfContributors())));
                esusuGroup.setCreatedByUsername(dto.getCreatedByUsername());
                esusuGroup.setCreationDate((dto.getCreationDate() != null && !dto.getCreationDate().trim().isEmpty()) ? formatter.parse(dto.getCreationDate()) : new Date());
                esusuGroup.setDescription(dto.getDescription());
                esusuGroup.setMinAmountLockedInMemberAccount(dto.getMinAmountLockedInMemberAccount());
                esusuGroup.setName(dto.getName());
                esusuGroup.setNumberOfContributors(dto.getNumberOfContributors());
                esusuGroup.setStartDate(formatter.parse(dto.getStartDate()));
                esusuGroup.setCircleCompleted(dto.getCircleEnded() != null ? dto.getCircleEnded() : false);
            }
        } catch (Exception ex) {
            logger.error("An error occured while converting from esusu group entity to esusu group dto. ", ex);
        }
        return esusuGroup;
    }
}
