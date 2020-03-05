/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller.businesslogic;

import com.starturn.database.entities.ContributionFrequency;
import com.starturn.database.entities.EsusuGroup;
import com.starturn.database.entities.EsusuGroupInvites;
import com.starturn.database.entities.EsusuGroupMembers;
import com.starturn.database.entities.MemberProfile;
import com.starturn.database.entities.UserToken;
import com.starturn.database.query.DaoServiceQuery;
import com.starturn.database.query.MemberServiceQuery;
import com.starturn.engine.controller.businesslogic.async.AsyncRunner;
import com.starturn.engine.facade.IAuthenticationFacade;
import com.starturn.engine.models.ChangePasswordDTO;
import com.starturn.engine.models.EsusuGroupDTO;
import com.starturn.engine.models.EsusuGroupInviteWrapper;
import com.starturn.engine.models.EsusuGroupInvitesDTO;
import com.starturn.engine.models.EsusuGroupMemberDTO;
import com.starturn.engine.models.EsusuGroupMembersWrapperDTO;
import com.starturn.engine.models.MemberProfileDTO;
import com.starturn.engine.models.response.ErrorMessage;
import com.starturn.engine.models.response.ResponseInformation;
import com.starturn.engine.util.DateUtility;
import com.starturn.engine.util.GeneralUtility;
import com.starturn.engine.util.PolicyValidator;
import com.starturn.engine.util.modelmapping.ModelMapping;
import com.starturn.engine.util.notification.model.EmailMessage;
import com.starturn.engine.util.notification.model.EmailPlaceholder;
import com.starturn.engine.util.notification.model.MessageDetails;
import com.starturn.engine.util.notification.thread.EmailAlertHelper;
import com.starturn.engine.util.notification.thread.SmsAlertHelper;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 *
 * @author Administrator
 */
@Component
public class RequestLogic {

    private static final Logger logger = LogManager.getLogger(RequestLogic.class);
    private @Autowired
    ErrorMessage errorMessage;
    private @Autowired
    PasswordEncoder passwordEncoder;
    private @Autowired
    MemberServiceQuery memberService;
    private @Autowired
    ModelMapping modelMapping;
    private @Autowired
    GeneralUtility util;
    private @Autowired
    EmailAlertHelper emailAlert;
    private @Autowired
    SmsAlertHelper smsAlert;
    private @Autowired
    DaoServiceQuery daoService;
    private @Autowired
    DateUtility dateUtility;
    private @Autowired
    IAuthenticationFacade authenticationFacade;
    private @Autowired
    AsyncRunner asyncRunner;

    public ResponseEntity<?> signUp(MemberProfileDTO dto, BindingResult result) throws Exception {

        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }

        if (memberService.checkUserExists(dto.getEmailAddress())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The email address is registered in the system already."));
        }

        if (memberService.checkPhoneNumberExists(dto.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The phone number is registered in the system already."));
        }

        MemberProfile profile = modelMapping.dtoToMember(dto);
        profile.setAtmCardExpiry("");
        profile.setAtmCardNo("");
        profile.setAtmCardType("");
        profile.setAtmCvv(0);
        profile.setAtmPin(0);
        profile.setBankAccountNumber("");
        profile.setBvn("");
        profile.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserToken token = new UserToken();

        token.setDateCreated(new Date());
        token.setExpired(Boolean.FALSE);
        token.setToken(util.getRandomNumberToken());
        token.setUsername(profile.getUsername());
        token.setValidated(Boolean.FALSE);

        String token_validity = "${token-validity}";

        boolean created = memberService.userSignUp(profile, token);
        if (!created) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }

        String message = "Your access token is " + token.getToken() + ". "
                + "Please note that it will expire in [" + token_validity + "] minutes time";

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            MessageDetails msgDetails = new MessageDetails();
            msgDetails.setFrom("DEMOCOOP");
            msgDetails.setTo(dto.getPhoneNumber());
            msgDetails.setText(message);

            smsAlert.sendSingleTextMessage(msgDetails);
        }

        if (dto.getEmailAddress() != null && !dto.getEmailAddress().trim().isEmpty()) {
            EmailPlaceholder placeHolder = new EmailPlaceholder();
            placeHolder.setFirst_name(dto.getName());
            placeHolder.setLast_name("");
            placeHolder.setCoop_name("Starturn");

            EmailMessage msgDetails = new EmailMessage();
            msgDetails.setSubject("Starturn Esusu Signup");
            msgDetails.setTo_email(dto.getEmailAddress());
            msgDetails.setEmailBody(message);
            msgDetails.setPlaceholder(placeHolder);

            emailAlert.sendSingleEmailOffice365(msgDetails);
        }

        return ResponseEntity.ok(new ResponseInformation("Sign up was successful"));
    }

    public ResponseEntity<?> validateToken(String token) throws Exception {
        if (!memberService.checkTokenExists(token)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token does not exist"));
        }
        UserToken user_token = memberService.retrieveToken(token);

        if (user_token.getExpired()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has expired."));
        }
        if (user_token.getValidated()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has been validated already."));
        }
        if (user_token.getExpired()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has expired."));

        }
        if (util.getDateDiffInMins(user_token.getDateCreated()) > 2) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has expired."));

        }

        List<Object> list_tosave = new ArrayList<>();
        user_token.setValidated(true);
        user_token.setDateValidated(new Date());
        list_tosave.add(user_token);

        MemberProfile profile = memberService.getUserInformation(user_token.getUsername());
        profile.setActive(Boolean.TRUE);
        list_tosave.add(profile);

        boolean saved = daoService.saveUpdateEntities(list_tosave);
        if (!saved) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to validate token, "
                            + "Please contact Administrator"));
        }

        return ResponseEntity.ok(new ResponseInformation("successful"));
    }

    public ResponseEntity<?> generateToken(String emailAddress) throws Exception {
        if (!memberService.checkUserExists(emailAddress)) {//replace with email exists method
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The email address does not exist in the system"));
        }

        MemberProfile memberProfile = memberService.getUserInformation(emailAddress);
        UserToken user_token = new UserToken();//credentialsService.getUserToken(token);

        if (user_token.getExpired()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has expired."));
        }
        if (user_token.getValidated()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has been validated already."));
        }
        if (util.getDateDiffInMins(user_token.getDateCreated()) > 2) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has expired."));

        }

        user_token.setMemberProfile(memberProfile);
        user_token.setDateCreated(new Date());
        user_token.setExpired(Boolean.FALSE);
        user_token.setToken(util.getRandomNumberToken());
        user_token.setUsername(emailAddress);
        user_token.setValidated(false);

        boolean saved = daoService.saveUpdateEntity(user_token);
        if (!saved) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to generate token, "
                            + "Please contact Administrator"));
        }

        String message = "Your account validation token is " + user_token.getToken();

        if (memberProfile.getPhoneNumber() != null && !memberProfile.getPhoneNumber().trim().isEmpty()) {
            MessageDetails msgDetails = new MessageDetails();
            msgDetails.setFrom("DEMOCOOP");
            msgDetails.setTo(memberProfile.getPhoneNumber());
            msgDetails.setText(message);

            smsAlert.sendSingleTextMessage(msgDetails);
        }

        if (memberProfile.getEmailAddress() != null && !memberProfile.getEmailAddress().trim().isEmpty()) {
            EmailPlaceholder placeHolder = new EmailPlaceholder();
            placeHolder.setFirst_name(memberProfile.getName());
            placeHolder.setLast_name("");
            placeHolder.setCoop_name("Starturn");

            EmailMessage msgDetails = new EmailMessage();
            msgDetails.setSubject("Account Validation Token");
            msgDetails.setTo_email(memberProfile.getEmailAddress());
            msgDetails.setEmailBody(message);
            msgDetails.setPlaceholder(placeHolder);

            emailAlert.sendSingleEmailOffice365(msgDetails);
        }

        return ResponseEntity.ok(new ResponseInformation("successful"));
    }

    public ResponseEntity<?> updateMemberProfile(MemberProfileDTO dto, BindingResult result) throws Exception {

        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }

        if (!memberService.checkUserExists(dto.getEmailAddress())) {//replace with email exists method
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The email address is registered in the system already."));
        }

        if (memberService.checkUserExists(dto.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The phone number is registered in the system already."));
        }

        MemberProfile profile = modelMapping.dtoToMember(dto);

        boolean created = daoService.saveUpdateEntity(profile);

        if (!created) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to update member account, "
                            + "Please contact Administrator"));
        }

        return ResponseEntity.ok(new ResponseInformation("successful"));
    }

    public ResponseEntity<?> createGroup(EsusuGroupDTO dto, BindingResult result) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }

        if (!memberService.checkUserExists(dto.getCreatedByUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The created by username does not exist in the system"));
        }

        if (dto.getContributionAmount().compareTo(new BigDecimal(0)) <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Contribution amount must be greater than zero."));
        }
        if (dto.getNumberOfContributors() <= 1) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Number of contributors must be greater than one"));
        }
        if (dto.getStartDate().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Start date must be specified in the format yyyy-mm-dd."));
        }
//        if (dto.getEndDate().trim().isEmpty()) {
//            return ResponseEntity.badRequest()
//                    .body(new ResponseInformation("End date must be specified in the format yyyy-mm-dd."));
//        }

        if (formatter.parse(dto.getEndDate()).before(formatter.parse(dto.getStartDate()))) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("End date must be after the start date"));
        }

        if (!daoService.checkObjectExists(ContributionFrequency.class, dto.getContributionFrequencyId())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified contribution frequency id is invalid"));
        }

        //calculate end date
        ContributionFrequency contributionFrequency = (ContributionFrequency) daoService.getEntity(ContributionFrequency.class, dto.getContributionFrequencyId());

        String freqName = contributionFrequency.getName();
        int frequencyId = dateUtility.getJavaDateCalendarFieldId(freqName);

        Date endDate = dateUtility.addToDate(formatter.parse(dto.getStartDate()), dto.getNumberOfContributors(), frequencyId);
        EsusuGroup esusu = modelMapping.dtoToEsusuGroup(dto);

        esusu.setEndDate(endDate);
        boolean created = daoService.saveUpdateEntity(esusu);

        if (!created) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to create group, "
                            + "Please contact Administrator"));
        }

        return ResponseEntity.ok(new ResponseInformation("Group creation was successful with the end date as " + endDate));

    }

    public ResponseEntity<?> inviteUsersToJoinGroup(EsusuGroupInviteWrapper dto, BindingResult result) {
        try {
            MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
            if (result.hasFieldErrors()) {
                String errors = result.getFieldErrors().stream()
                        .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
                return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
            }
            if (!daoService.checkObjectExists(EsusuGroup.class, dto.getGroupId())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified esusu group id is invalid"));
            }
            EsusuGroup group = (EsusuGroup) daoService.getEntity(EsusuGroup.class, dto.getGroupId());

            if (group.getCircleCompleted()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("You cannot invite users to a group with a completed circle."));
            }

            if (dto.getUsernames().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("You must pass the username of the users to invite."));
            }

            if (dto.getUsernames().size() > group.getNumberOfContributors()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("You are not allowed to add more than the number of required contributors."));
            }

            List<String> not_found = new ArrayList<>();
            Set<String> repeating_ids = new HashSet<>();

            for (String username : dto.getUsernames()) {
                if (!memberService.checkUserExists(username)) {
                    not_found.add(username);
                }
                if (!repeating_ids.add(username)) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseInformation("The username, " + username
                                    + ", appears to be repeating, duplicates are not allowed."));
                }
            }
            if (!not_found.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("No match found for these username(s) - " + not_found));
            }
            List<EsusuGroupInvites> invites_list = new ArrayList<>();

            for (String username : dto.getUsernames()) {
                EsusuGroupInvites invite = new EsusuGroupInvites();
                MemberProfile member = memberService.getUserInformation(username);
                invite.setAccepted(Boolean.FALSE);
                invite.setEsusuGroup(group);
                invite.setInvitedByUsername(initiator.getUsername());
                invite.setInvitedDate(new Date());
                invite.setMemberProfile(member);
                invite.setRejected(Boolean.FALSE);
                invites_list.add(invite);
            }

            boolean saved = daoService.saveUpdateEntities(invites_list);
            if (!saved) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("Unable to invite users to join group, "
                                + "Please contact Administrator"));
            }

            for (String username : dto.getUsernames()) {
                MemberProfile memberProfile = memberService.getUserInformation(username);
                String message = "Dear " + memberProfile.getName() + ", you have been invited to join the group " + group.getName() + ","
                        + " please login to the system and accept the invitation.";
                if (memberProfile.getEmailAddress() != null && !memberProfile.getEmailAddress().trim().isEmpty()) {
                    EmailPlaceholder placeHolder = new EmailPlaceholder();
                    placeHolder.setFirst_name(memberProfile.getName());
                    placeHolder.setLast_name("");
                    placeHolder.setCoop_name("Starturn");

                    EmailMessage msgDetails = new EmailMessage();
                    msgDetails.setSubject("Group Invitation");
                    msgDetails.setTo_email(memberProfile.getEmailAddress());
                    msgDetails.setEmailBody(message);
                    msgDetails.setPlaceholder(placeHolder);

                    emailAlert.sendSingleEmailOffice365(msgDetails);
                }
            }

        } catch (Exception ex) {
            logger.error("An error occured while inviting people to join esusu group. ", ex);
        }
        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    /**
     * treats invitation request to join a group, status true means accepted
     * while false means rejected
     *
     * @param invitationId the invitation id
     * @param status the acceptance / rejection status
     * @return response to the user after treating the request
     * @throws Exception
     */
    public ResponseEntity<?> treatGroupInvitation(Integer invitationId, Boolean status) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        if (!daoService.checkObjectExists(EsusuGroupInvites.class, invitationId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified group invitation id is invalid"));
        }
        if (status == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Status must be either true or false"));
        }

        EsusuGroupInvites invite = (EsusuGroupInvites) daoService.getEntity(EsusuGroupInvites.class, invitationId);
        EsusuGroup group = (EsusuGroup) daoService.getEntity(EsusuGroup.class, invite.getEsusuGroup().getId());

        if (!Objects.equals(invite.getMemberProfile().getId(), initiator.getId())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("You are not allowed to treat another user's request."));
        }

        if (group.getCircleCompleted()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The circle of the group's invitation has completed already, you cannot treat it anymore."));
        }
        EsusuGroupMembers group_member = new EsusuGroupMembers();
        List<Object> tosave = new ArrayList<>();

        if (!status) {
            invite.setRejected(status);
        } else {
            invite.setAccepted(status);
            group_member.setAmountPaid(BigDecimal.ZERO);
            group_member.setCollectionPosition("");
            group_member.setCreatedByUsername(group.getCreatedByUsername());
            group_member.setCreationDate(new Date());
            group_member.setEsusuGroup(group);
            group_member.setExpectedAmount(group.getMonthlyCollectionAmount());
            group_member.setExpectedCollectionDate(null);
            group_member.setMemberProfile(initiator);
            group_member.setPaid(false);
            tosave.add(group_member);
        }
        invite.setResponseDate(new Date());
        tosave.add(invite);

        boolean updated = daoService.saveUpdateEntities(tosave);
        if (!updated) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to attend to group invitation, "
                            + "Please contact Administrator"));
        }
        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    public ResponseEntity<?> viewMemberGroupInvitations(Integer memberProfileId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        if (!daoService.checkObjectExists(MemberProfile.class, memberProfileId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified member id is invalid"));
        }

        List<EsusuGroupInvites> invites = memberService.viewMemberGroupInvitation(memberProfileId);
        if (invites.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("There are no pending group invitations for you."));
        }
        List<EsusuGroupInvitesDTO> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDTO dto = new EsusuGroupInvitesDTO();
            dto.setId(inv.getId());
            dto.setAccepted(inv.getAccepted());
            dto.setRejected(inv.getRejected());
            dto.setEsusuGroupId(inv.getEsusuGroup().getId());
            dto.setInvitedByUsername(inv.getInvitedByUsername());
            dto.setEmailAddress(member.getEmailAddress());
            list_out.add(dto);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewGroupInvitations(Integer groupId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        if (!daoService.checkObjectExists(EsusuGroup.class, groupId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified group id is invalid"));
        }

        List<EsusuGroupInvites> invites = memberService.viewGroupInvitations(groupId);
        if (invites.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("There are no invitations for this group."));
        }

        List<EsusuGroupInvitesDTO> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDTO dto = new EsusuGroupInvitesDTO();
            dto.setId(inv.getId());
            dto.setAccepted(inv.getAccepted());
            dto.setRejected(inv.getRejected());
            dto.setEsusuGroupId(inv.getEsusuGroup().getId());
            dto.setInvitedByUsername(inv.getInvitedByUsername());
            dto.setEmailAddress(member.getEmailAddress());
            list_out.add(dto);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewAcceptedGroupInvitations(Integer groupId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        if (!daoService.checkObjectExists(EsusuGroup.class, groupId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified group id is invalid"));
        }

        List<EsusuGroupInvites> invites = memberService.viewAllAcceptedGroupInvitations(groupId);
        if (invites.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("There are no invitations for this group."));
        }

        List<EsusuGroupInvitesDTO> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDTO dto = new EsusuGroupInvitesDTO();
            dto.setId(inv.getId());
            dto.setAccepted(inv.getAccepted());
            dto.setRejected(inv.getRejected());
            dto.setEsusuGroupId(inv.getEsusuGroup().getId());
            dto.setInvitedByUsername(inv.getInvitedByUsername());
            dto.setEmailAddress(member.getEmailAddress());
            list_out.add(dto);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewRejectedGroupInvitations(Integer groupId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        if (!daoService.checkObjectExists(EsusuGroup.class, groupId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified group id is invalid"));
        }

        List<EsusuGroupInvites> invites = memberService.viewAllRejectedGroupInvitations(groupId);
        if (invites.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("There are no invitations for this group."));
        }

        List<EsusuGroupInvitesDTO> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDTO dto = new EsusuGroupInvitesDTO();
            dto.setId(inv.getId());
            dto.setRejected(inv.getRejected());
            dto.setEsusuGroupId(inv.getEsusuGroup().getId());
            dto.setInvitedByUsername(inv.getInvitedByUsername());
            dto.setEmailAddress(member.getEmailAddress());
            list_out.add(dto);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> login() throws Exception {
        MemberProfile memberProfile = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        asyncRunner.processMemberLastLogin(memberProfile.getId());

        MemberProfileDTO memberProfileDTO = modelMapping.memberToDtoMapping(memberProfile);

        return ResponseEntity.ok(memberProfileDTO);
    }

    public ResponseEntity<?> viewUserDetails(Integer profileId) throws Exception {
        if (!daoService.checkObjectExists(MemberProfile.class, profileId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The requested profile does not exist"));
        }

        MemberProfile memberProfileToView = (MemberProfile) daoService.getEntity(MemberProfile.class, profileId);
        if (memberProfileToView.getAccountClosed()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This account has been closed, please contact the admin."));
        }
        if (!memberProfileToView.getActive()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This account is yet to be activated, please contact the admin."));
        }
        MemberProfileDTO userDetails = modelMapping.memberToDtoMapping(memberProfileToView);

        return ResponseEntity.ok(userDetails);
    }

    public ResponseEntity<?> resetPassword(String username) throws Exception {
        if (!memberService.checkUserExists(username)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The user does not exist"));
        }
        MemberProfile memberProfile = memberService.getUserInformation(username);
        if (memberProfile.getAccountClosed()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This account has been closed, please contact the admin."));
        }
        if (!memberProfile.getActive()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This account is yet to be activated, please contact the admin."));
        }

        char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[8];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(charset.length);
            result[i] = charset[randomCharIndex];
        }
        String randomPassword = new String(result);
        String hashedRandomPassword = passwordEncoder.encode(randomPassword);

        memberProfile.setPassword(hashedRandomPassword);
        if (!memberProfile.getFirstTime()) {
            memberProfile.setFirstTime(true);
        }

        boolean saved = daoService.saveUpdateEntity(memberProfile);
        if (!saved) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }

        String emailAddress = memberProfile.getEmailAddress();

        String message = "Your password has been reset successfully. "
                + "Your password is now " + randomPassword + ". "
                + "Please, login to change to your preferred password.";

        if (emailAddress != null && !emailAddress.trim().isEmpty()) {
            EmailPlaceholder placeHolder = new EmailPlaceholder();
            placeHolder.setFirst_name(memberProfile.getName());
            placeHolder.setLast_name(memberProfile.getName());
            placeHolder.setCoop_name("Starturn");

            EmailMessage msgDetails = new EmailMessage();
            msgDetails.setSubject("Reset password");
            msgDetails.setTo_email(emailAddress);
            msgDetails.setEmailBody(message);
            msgDetails.setPlaceholder(placeHolder);

            emailAlert.sendSingleEmailOffice365(msgDetails);
        }

        if (memberProfile.getPhoneNumber() != null && !memberProfile.getPhoneNumber().trim().isEmpty()) {
            MessageDetails msgDetails = new MessageDetails();
            msgDetails.setFrom("DEMOCOOP");
            msgDetails.setTo(memberProfile.getPhoneNumber());
            msgDetails.setText(message);

            smsAlert.sendSingleTextMessage(msgDetails);
        }

        return ResponseEntity.ok(new ResponseInformation("successful"));
    }

    public ResponseEntity<?> changePassword(ChangePasswordDTO passwordInfo, BindingResult result) throws Exception {
        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }

        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        System.out.println("current password from ACCOUNT: " + initiator.getPassword());
        System.out.println("current password from API: " + passwordInfo.getOldPassword());

        if (!passwordEncoder.matches(passwordInfo.getOldPassword(), initiator.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Your old password is incorrect. Please, try again"));
        }

        if (!passwordInfo.getPassword().equals(passwordInfo.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Your password and confirmation password do not match. Please, try again"));
        }

        if (passwordEncoder.matches(passwordInfo.getPassword(), initiator.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("You are not allowed to use the old password again. Please, try again"));
        }

        if (passwordInfo.getIsfirstTime() == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Is first time flag must be set."));
        }

        int passwordLength = 6;

        //PASSWORD POLICY
        //there must be a digit (?=.*\\d)
        //there must be a small letter (?=.*[a-z])
        //there must be a capital letter (?=.*[A-Z])
        //there must be a special character (?=.*[!@#$%&])
        //all the above can be placed anywhere within the password, hence the .*
        //all the above combined must make up 8 characters or up to 20 characters, assuming the password length is 8
        String policy = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%&]).{" + passwordLength + ",20})";

        if (!PolicyValidator.validatePassword(passwordInfo.getPassword(), policy)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Your password does not meet the system's password policy. "
                            + "Passwords must be " + passwordLength + " or longer, and must contain a digit, "
                            + "a small letter, a capital letter, and any of the following special characters: "
                            + "!@#$%&"));
        }

        String hashedRandomPassword = passwordEncoder.encode(passwordInfo.getPassword());
        initiator.setPassword(hashedRandomPassword);

        if (initiator.getFirstTime()) {
            initiator.setFirstTime(false);
        }

        boolean saved = daoService.saveUpdateEntity(initiator);
        if (!saved) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }

        String emailAddress = initiator.getEmailAddress();
        String message = "Your password has been successfully changed.";

        if (emailAddress != null && !emailAddress.trim().isEmpty()) {
            EmailPlaceholder placeHolder = new EmailPlaceholder();
            placeHolder.setFirst_name(initiator.getName());
            placeHolder.setLast_name(initiator.getName());
            placeHolder.setCoop_name("Starturn");

            EmailMessage msgDetails = new EmailMessage();
            msgDetails.setSubject("Change password");
            msgDetails.setTo_email(emailAddress);
            msgDetails.setEmailBody(message);
            msgDetails.setPlaceholder(placeHolder);

            emailAlert.sendSingleEmailOffice365(msgDetails);
        }
        String phoneNumber = initiator.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            MessageDetails msgDetails = new MessageDetails();
            msgDetails.setFrom("DEMOCOOP");
            msgDetails.setTo(phoneNumber);
            msgDetails.setText(message);

            smsAlert.sendSingleTextMessage(msgDetails);
        }

        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    public ResponseEntity<?> prepareGroupSavingsCollectionDate(EsusuGroupMembersWrapperDTO dto, BindingResult result) throws Exception {
        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        if (dto.getGroupMembers().size() < 1) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The esusu group members list cannot be empty, "
                            + "Please contact Administrator"));
        }
        if (!daoService.checkObjectExists(EsusuGroup.class, dto.getGroupMembers().get(0).getEsusuGroupId())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified group id is invalid."));
        }

        int level = 1;
        List<EsusuGroupMembers> list_to_update = new ArrayList<>();
        Date startDate = formatter.parse(dto.getCollectionDate());

        EsusuGroup group_hib = (EsusuGroup) daoService.getEntity(EsusuGroup.class, dto.getGroupMembers().get(0).getEsusuGroupId());
        ContributionFrequency contributionFrequency = (ContributionFrequency) daoService.getEntity(ContributionFrequency.class, group_hib.getContributionFrequency().getId());

        if (!initiator.getUsername().equalsIgnoreCase(group_hib.getCreatedByUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Only the creator of this group is allowed to perform this function."));
        }
        
        String freqName = contributionFrequency.getName();
        int frequencyId = dateUtility.getJavaDateCalendarFieldId(freqName);
        Set<Integer> repeating_ids = new HashSet<>();

        for (EsusuGroupMemberDTO egm : dto.getGroupMembers()) {
            Date collectionDate = dateUtility.addToDate(startDate, 1, frequencyId);

            if (!repeating_ids.add(egm.getMemberProfileId())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified member id [" + egm.getMemberProfileId() + "], is repeating, please remove duplicates."));
            }

            if (!daoService.checkObjectExists(EsusuGroupMembers.class, egm.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified id [" + egm.getId() + "], does not exist."));
            }

            if (!daoService.checkObjectExists(MemberProfile.class, egm.getMemberProfileId())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified member id [" + egm.getMemberProfileId() + "], does not exist."));
            }

            if (egm.getCollectionPosition() == null || egm.getCollectionPosition() != level) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("A collection position contains an illegal position number. "
                                + "It should be " + level + ", whereas it is " + egm.getCollectionPosition()));
            }
            EsusuGroupMembers group_member = (EsusuGroupMembers) daoService.getEntity(EsusuGroupMembers.class, egm.getId());
            group_member.setCollectionPosition(String.valueOf(egm.getCollectionPosition()));

            if (level == 1) {
                group_member.setExpectedCollectionDate(startDate);
            } else {
                group_member.setExpectedCollectionDate(collectionDate);
            }

            list_to_update.add(group_member);
            level++;

        }
        boolean created = daoService.saveUpdateEntities(list_to_update);

        if (!created) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to persist changes due to error, "
                            + "Please contact Administrator"));
        }
        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }
}
