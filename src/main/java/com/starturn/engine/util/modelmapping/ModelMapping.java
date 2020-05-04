/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.modelmapping;

import com.starturn.engine.database.entities.ContributionFrequency;
import com.starturn.engine.database.entities.EsusuGroup;
import com.starturn.engine.database.entities.EsusuGroupMembers;
import com.starturn.engine.database.entities.EsusuRepaymentSchedule;
import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.query.factory.ServiceQueryFactory;
import com.starturn.engine.models.EsusuGroupDTO;
import com.starturn.engine.models.EsusuGroupMemberDto;
import com.starturn.engine.models.EsusuRepaymentScheduleDto;
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
            dto.setDob(memberProfile.getDob() != null ? formatter.format(memberProfile.getDob()) : "");

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
            memberProfile.setAccountClosed(false);
            memberProfile.setAccountSuspended(false);
            memberProfile.setLocked(false);
            memberProfile.setFirstTime(dto.getFirstTime() != null ? dto.getFirstTime() : false);
            memberProfile.setAccpetedTermsCondition(dto.getAccpetedTermsCondition());
            memberProfile.setCreationDate(!"".equals(dto.getCreationDate()) ? formatter.parse(dto.getCreationDate()) : new Date());
            memberProfile.setGender(dto.getGender());
            memberProfile.setActive(dto.getActive() != null ? dto.getActive() : false);
            if (dto.getDob() != null && !dto.getDob().isEmpty()) {
                memberProfile.setDob(formatter.parse(dto.getDob()));
            }
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
                dto.setPositionArranged(esusuGroup.getPositionArranged() != null ? esusuGroup.getPositionArranged() : false);
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
                esusuGroup.setPositionArranged(dto.getPositionArranged() != null ? dto.getPositionArranged() : false);
                
            }
        } catch (Exception ex) {
            logger.error("An error occured while converting from esusu group entity to esusu group dto. ", ex);
        }
        return esusuGroup;
    }

    public EsusuGroupMemberDto esusuGroupMemberToDto(EsusuGroupMembers group_member) {
        EsusuGroupMemberDto dto = new EsusuGroupMemberDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (group_member != null) {
                MemberProfile memberProfile = (MemberProfile) ServiceQueryFactory.getDaoServiceQuery().
                        getEntity(MemberProfile.class, group_member.getMemberProfile().getId());
                dto.setId(group_member.getId());
                dto.setAmountPaid(group_member.getAmountPaid());
                dto.setCollectionPosition(Integer.parseInt(group_member.getCollectionPosition()));
                dto.setCreatedByUsername(group_member.getCreatedByUsername());
                dto.setCreationDate(formatter.format(group_member.getCreationDate()));
                dto.setEsusuGroupId(group_member.getEsusuGroup().getId());
                dto.setExpectedAmount(group_member.getExpectedAmount());
                dto.setExpectedCollectionDate(formatter.format(group_member.getExpectedCollectionDate()));
                dto.setMemberProfileId(group_member.getMemberProfile().getId());
                dto.setPaid(group_member.getPaid());
                dto.setMemberName(memberProfile.getName());
                dto.setInterestAmountToPayback(group_member.getInterestAmountToPayback());
                dto.setInterestAmountToReceive(group_member.getInterestAmountToReceive());
                dto.setMonthlyInterestAmountPayback(group_member.getMonthlyInterestAmountPayback());
                dto.setNumberOfPaybackSchedules(group_member.getNumberOfPaybackSchedules());
                dto.setTotalAmountToReceive(group_member.getTotalAmountToReceive());
                dto.setInterestPaid(group_member.getInterestPaid());
                dto.setPaidDate(group_member.getPaidDate() != null ? formatter.format(group_member.getPaidDate()) : "");

            }

        } catch (Exception ex) {
            logger.error("An error occured while converting from esusu group member to esusu group member dto. ", ex);
        }
        return dto;
    }

    public EsusuRepaymentScheduleDto EsusuRepaymentScheduleToDto(EsusuRepaymentSchedule entity) {
        EsusuRepaymentScheduleDto dto = new EsusuRepaymentScheduleDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (entity != null) {
                dto.setId(entity.getId());
                dto.setEsusuGroupId(entity.getEsusuGroup().getId());
                dto.setEsusuGroupMemberId(entity.getId());
                dto.setInterestAmount(entity.getInterestAmount());
                dto.setMemberProfileId(entity.getMemberProfile().getId());
                dto.setPrincipalAmount(entity.getPrincipalAmount());
                dto.setPaid(entity.getPaid());
                dto.setPaidDate(entity.getPaidDate() != null ? formatter.format(entity.getPaidDate()) : "");
                dto.setRepaymentDate(entity.getRepaymentDate() != null ? formatter.format(entity.getRepaymentDate()) : "");
                dto.setTotalAmount(entity.getTotalAmount());

            }
        } catch (Exception ex) {
            logger.error("An error occured while converting from esusu repayment schedule to dto. ", ex);
        }
        return dto;
    }

}
