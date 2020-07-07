/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller.businesslogic;

import com.starturn.engine.database.entities.ContributionFrequency;
import com.starturn.engine.database.entities.EsusuGroup;
import com.starturn.engine.database.entities.EsusuGroupInvites;
import com.starturn.engine.database.entities.EsusuGroupMembers;
import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.entities.UserToken;
import com.starturn.engine.database.query.DaoServiceQuery;
import com.starturn.engine.database.query.MemberServiceQuery;
import com.starturn.engine.controller.businesslogic.async.AsyncRunner;
import com.starturn.engine.database.entities.EsusuRepaymentSchedule;
import com.starturn.engine.database.entities.InterestDisbursementType;
import com.starturn.engine.database.entities.MemberProfilePicture;
import com.starturn.engine.database.entities.MemberWallet;
import com.starturn.engine.facade.IAuthenticationFacade;
import com.starturn.engine.models.ArrangeGroupMemberCollectionPositionDto;
import com.starturn.engine.models.ArrangeGroupMemberCollectionPositionWrapperDto;
import com.starturn.engine.models.BalanceDTO;
import com.starturn.engine.models.ChangePasswordDTO;
import com.starturn.engine.models.ContributionFrequencyDto;
import com.starturn.engine.models.EsusuGroupDTO;
import com.starturn.engine.models.EsusuGroupInviteWrapper;
import com.starturn.engine.models.EsusuGroupInvitesDto;
import com.starturn.engine.models.EsusuGroupMemberDto;
import com.starturn.engine.models.EsusuGroupMembersWrapperDto;
import com.starturn.engine.models.EsusuRepaymentScheduleDto;
import com.starturn.engine.models.InterestDisbursementTypeDto;
import com.starturn.engine.models.MemberProfileDTO;
import com.starturn.engine.models.MemberProfilePictureDTO;
import com.starturn.engine.models.MemberWalletDto;
import com.starturn.engine.models.response.ErrorMessage;
import com.starturn.engine.models.response.ResponseInformation;
import com.starturn.engine.multipart.PictureFileStorageService;
import com.starturn.engine.util.BytesConverter;
import com.starturn.engine.util.DateUtility;
import com.starturn.engine.util.GeneralUtility;
import com.starturn.engine.util.PolicyValidator;
import com.starturn.engine.util.modelmapping.ModelMapping;
import com.starturn.engine.util.notification.model.EmailMessage;
import com.starturn.engine.util.notification.model.EmailPlaceholder;
import com.starturn.engine.util.notification.model.MessageDetails;
import com.starturn.engine.util.notification.thread.EmailAlertHelper;
import com.starturn.engine.util.notification.thread.SmsAlertHelper;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

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
    @Value("${file.profile-pic-dir}")
    private String profilePictureBasePath;
    private @Autowired
    PictureFileStorageService pictureFileStorageService;
    private @Autowired
    BytesConverter converter;

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

//        if (memberService.checkPhoneNumberExists(dto.getPhoneNumber())) {
//            return ResponseEntity.badRequest()
//                    .body(new ResponseInformation("The phone number is registered in the system already."));
//        }
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

        boolean created = memberService.userSignUp(profile, token);
        if (!created) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }

        String message = "Starturn confirmation token is " + token.getToken();

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            MessageDetails msgDetails = new MessageDetails();
            msgDetails.setFrom("Eazcoopdemo");
            msgDetails.setTo(dto.getPhoneNumber());
            msgDetails.setText(message);

            smsAlert.sendSingleTextMessage(msgDetails);
            logger.info("sent text message to: {} ", dto.getPhoneNumber());
        }

        if (dto.getEmailAddress() != null && !dto.getEmailAddress().trim().isEmpty()) {
            EmailPlaceholder placeHolder = new EmailPlaceholder();
            placeHolder.setFirst_name(dto.getName());
            placeHolder.setLast_name("");
            placeHolder.setCoop_name("Starturn");
            placeHolder.setUsername(dto.getUsername());
            placeHolder.setPassword(dto.getPassword());
            placeHolder.setToken(token.getToken());

            EmailMessage msgDetails = new EmailMessage();
            msgDetails.setSubject("Starturn Esusu Signup");
            msgDetails.setTo_email(dto.getEmailAddress());
            msgDetails.setEmailBody(message);
            msgDetails.setTemplate("create-member");
            msgDetails.setPlaceholder(placeHolder);

            emailAlert.sendSingleEmailOffice365(msgDetails);
            logger.info("sent email message to: {} ", dto.getEmailAddress());
        }
        MemberProfile newProfile = memberService.getUserInformation(profile.getUsername());
        return ResponseEntity.ok(modelMapping.memberToDtoMapping(newProfile));
    }

    public ResponseEntity<?> validateToken(String token) throws Exception {
        if (!memberService.checkTokenExists(token)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token does not exist"));
        }
        UserToken user_token = memberService.retrieveToken(token);
        long token_expired_minutes = util.getDateDiffInMins(user_token.getDateCreated());
        if (token_expired_minutes > 5) {
            user_token.setExpired(Boolean.TRUE);
            daoService.saveUpdateEntity(user_token);
        }

        if (user_token.getExpired()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has expired."));
        }
        if (user_token.getValidated()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The token has been validated already."));
        }

        MemberProfile profile = memberService.getUserInformation(user_token.getUsername());
        if (profile.getActive()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Your account is already active."));
        }

        List<Object> list_tosave = new ArrayList<>();
        user_token.setValidated(true);
        user_token.setDateValidated(new Date());
        list_tosave.add(user_token);

        profile.setActive(Boolean.TRUE);
        list_tosave.add(profile);

        MemberWallet wallet = new MemberWallet();
        wallet.setActive(Boolean.TRUE);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatioDate(new Date());
        wallet.setMemberProfile(profile);
        list_tosave.add(wallet);

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
        UserToken user_token = new UserToken();

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

        String message = "Starturn confirmation token is " + user_token.getToken();

        if (memberProfile.getPhoneNumber() != null && !memberProfile.getPhoneNumber().trim().isEmpty()) {
            MessageDetails msgDetails = new MessageDetails();
            msgDetails.setFrom("Eazcoopdemo");
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

        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    public ResponseEntity<?> updateMemberProfile(MemberProfileDTO dto, BindingResult result) throws Exception {

        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }

        if (!daoService.checkObjectExists(MemberProfile.class, dto.getId())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified member id does not exist."));
        }
        MemberProfile initiator = (MemberProfile) daoService.getEntity(MemberProfile.class, dto.getId());
        if ((dto.getEmailAddress().trim().isEmpty() && !initiator.getEmailAddress().equalsIgnoreCase(dto.getEmailAddress())) && memberService.checkUserExists(dto.getEmailAddress())) {//replace with email exists method
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The email address is registered in the system already."));
        }

        if (!initiator.getPhoneNumber().equals(dto.getPhoneNumber()) && memberService.checkUserExists(dto.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The phone number is registered in the system already."));
        }

        MemberProfile profile = modelMapping.dtoToMember(dto);
        logger.info("date of birth {} ", profile.getDob());

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
        if (!daoService.checkObjectExists(InterestDisbursementType.class, dto.getInterest_disbursement_type_id())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified interest disbursement type id is invalid"));
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

            dto.getUsernames().add(group.getCreatedByUsername());

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

        if (invite.getTreated() != null && invite.getTreated()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This request has already been attended to."));
        }
        if (!Objects.equals(invite.getMemberProfile().getId(), initiator.getId())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("You are not allowed to treat another user's invitation."));
        }
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
            invite.setRejected(true);
        } else {
            invite.setAccepted(status);
            group_member.setAmountPaid(BigDecimal.ZERO);
            group_member.setCollectionPosition("");
            group_member.setCreatedByUsername(group.getCreatedByUsername());
            group_member.setCreationDate(new Date());
            group_member.setEsusuGroup(group);
            group_member.setExpectedAmount(group.getMonthlyCollectionAmount());
            group_member.setExpectedCollectionDate(null);
            group_member.setMemberProfile(invite.getMemberProfile());
            group_member.setPaid(false);
            group_member.setInterestAmountToReceive(BigDecimal.ZERO);
            group_member.setInterestPaid(false);
            group_member.setMonthlyInterestAmountPayback(BigDecimal.ZERO);
            group_member.setInterestAmountToPayback(BigDecimal.ZERO);
            group_member.setNumberOfPaybackSchedules(0);
            tosave.add(group_member);
        }

        invite.setResponseDate(new Date());
        invite.setTreated(true);
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
        List<EsusuGroupInvitesDto> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            EsusuGroup group = (EsusuGroup)daoService.getEntity(EsusuGroup.class, inv.getEsusuGroup().getId());
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDto dto = new EsusuGroupInvitesDto();
            dto.setId(inv.getId());
            dto.setAccepted(inv.getAccepted());
            dto.setRejected(inv.getRejected());
            dto.setEsusuGroupId(inv.getEsusuGroup().getId());
            dto.setInvitedByUsername(inv.getInvitedByUsername());
            dto.setEmailAddress(member.getEmailAddress());
            dto.setGroupName(group.getName());

            if (memberService.checkMemberHasProfilePicture(member.getId())) {
                MemberProfilePicture picture = memberService.getMemberProfilePicture(member.getId());
                File file = new File(picture.getPicturepath());
                byte[] read = Files.readAllBytes(file.toPath());
                String encodedContents = converter.encodeToString(read);

                MemberProfilePictureDTO dto_pic = new MemberProfilePictureDTO();
                //dto.setFileName(picture.getPicturepath());
                dto_pic.setFileContent(encodedContents);
                dto_pic.setMemberProfileId(member.getId());
                dto_pic.setMemberName(member.getName());
                dto.setProfilePicture(dto_pic);
            }
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

        List<EsusuGroupInvitesDto> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDto dto = new EsusuGroupInvitesDto();
            dto.setId(inv.getId());
            dto.setAccepted(inv.getAccepted());
            dto.setRejected(inv.getRejected());
            dto.setEsusuGroupId(inv.getEsusuGroup().getId());
            dto.setInvitedByUsername(inv.getInvitedByUsername());
            dto.setEmailAddress(member.getEmailAddress());

            if (memberService.checkMemberHasProfilePicture(member.getId())) {
                MemberProfilePicture picture = memberService.getMemberProfilePicture(member.getId());
                File file = new File(picture.getPicturepath());
                byte[] read = Files.readAllBytes(file.toPath());
                String encodedContents = converter.encodeToString(read);

                MemberProfilePictureDTO dto_pic = new MemberProfilePictureDTO();
                //dto.setFileName(picture.getPicturepath());
                dto_pic.setFileContent(encodedContents);
                dto_pic.setMemberProfileId(member.getId());
                dto_pic.setMemberName(member.getName());
                dto.setProfilePicture(dto_pic);
            }
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

        List<EsusuGroupInvitesDto> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDto dto = new EsusuGroupInvitesDto();
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

        List<EsusuGroupInvitesDto> list_out = new ArrayList<>();
        for (EsusuGroupInvites inv : invites) {
            MemberProfile member = (MemberProfile) daoService.getEntity(MemberProfile.class, inv.getMemberProfile().getId());

            EsusuGroupInvitesDto dto = new EsusuGroupInvitesDto();
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

    public ResponseEntity<?> prepareGroupSavingsCollectionDate(EsusuGroupMembersWrapperDto dto, BindingResult result) throws Exception {
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

        for (EsusuGroupMemberDto egm : dto.getGroupMembers()) {
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

    public ResponseEntity<?> acceptTermsAndConditions() throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        initiator.setAccpetedTermsCondition(Boolean.TRUE);

        boolean saved = daoService.saveUpdateEntity(initiator);
        if (!saved) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }
        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    public ResponseEntity<?> arrangeEsusGroupCollection(EsusuGroupMembersWrapperDto dto, BindingResult result) throws Exception {
        if (result.hasFieldErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(p -> p.getDefaultMessage()).collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ResponseInformation("An error occured while trying to persist information: " + errors));
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        if (dto.getGroupMembers() == null || dto.getGroupMembers().size() < 1) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The esusu group members list cannot be empty, "
                            + "Please contact Administrator"));
        }
        if (!daoService.checkObjectExists(EsusuGroup.class, dto.getGroupMembers().get(0).getEsusuGroupId())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified group id is invalid."));
        }
        EsusuGroup group = (EsusuGroup) daoService.getEntity(EsusuGroup.class, dto.getGroupMembers().get(0).getEsusuGroupId());
        if (group.getPositionArranged() != null && group.getPositionArranged()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This action has been performed already."));
        }
        int level = 1;
        //Date startDate = formatter.parse(dto.getCollectionDate());
        Date startDate = group.getStartDate();

        ContributionFrequency contributionFrequency = (ContributionFrequency) daoService.getEntity(ContributionFrequency.class, group.getContributionFrequency().getId());

        if (!initiator.getUsername().equalsIgnoreCase(group.getCreatedByUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Only the creator of this group is allowed to perform this function."));
        }

        String freqName = contributionFrequency.getName();
        int frequencyId = dateUtility.getJavaDateCalendarFieldId(freqName);
        Set<Integer> repeating_ids = new HashSet<>();
        int totalMembers = dto.getGroupMembers().size();
        Map<EsusuGroupMembers, List<EsusuRepaymentSchedule>> records_to_create = new HashMap<>();
        if (group.getIsWithInterest()) {//esusu with interest
            BigDecimal monthlyInterestAmountToPayback;
            int interestRate_group = 5;
            int interestRate_operator = 50;

            List<EsusuRepaymentSchedule> all_schedules;
            Date newStartDate = startDate;
            for (EsusuGroupMemberDto egm : dto.getGroupMembers()) {
                Date collectionDate = dateUtility.addToDate(newStartDate, 1, frequencyId);

                if (!repeating_ids.add(egm.getMemberProfileId())) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseInformation("The specified member id [" + egm.getMemberProfileId() + "], is repeating, please remove duplicates."));
                }

                if (!daoService.checkObjectExists(EsusuGroupMembers.class, egm.getId())) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseInformation("The specified esusu group member id [" + egm.getId() + "], does not exist."));
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
                monthlyInterestAmountToPayback = (group.getContributionAmount().multiply(BigDecimal.valueOf(interestRate_group))).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);

                BigDecimal interest_available = ((BigDecimal.valueOf(level).subtract(BigDecimal.ONE)).multiply(BigDecimal.valueOf(interestRate_group)).multiply(group.getContributionAmount())).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
                BigDecimal operator_fee = ((interest_available).multiply(BigDecimal.valueOf(interestRate_operator))).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
                BigDecimal interest_to_receive = interest_available.subtract(operator_fee);
                logger.info("level {}, available interest {}, operator fee {}, interest to receive {} ",
                        level, interest_available, operator_fee, interest_to_receive);

                EsusuGroupMembers group_member = (EsusuGroupMembers) daoService.getEntity(EsusuGroupMembers.class, egm.getId());
                group_member.setCollectionPosition(String.valueOf(egm.getCollectionPosition()));
                group_member.setExpectedAmount(BigDecimal.valueOf(totalMembers).multiply(group.getMonthlyCollectionAmount()));
                group_member.setAmountPaid(BigDecimal.ZERO);
                group_member.setPaid(Boolean.FALSE);
                group_member.setExpectedCollectionDate(collectionDate);
                group_member.setExpectedAmount(group.getContributionAmount().multiply(BigDecimal.valueOf(totalMembers)));
                group_member.setInterestAmountToReceive(interest_to_receive);
                group_member.setInterestPaid(Boolean.FALSE);
                group_member.setTotalAmountToReceive(group_member.getExpectedAmount().add(interest_to_receive));
                group_member.setMonthlyInterestAmountPayback(monthlyInterestAmountToPayback);
                int number_of_schedules = totalMembers - level;
                group_member.setNumberOfPaybackSchedules(number_of_schedules);
                group_member.setInterestAmountToPayback(monthlyInterestAmountToPayback.multiply(BigDecimal.valueOf(number_of_schedules)));
                Date schedule_start_date = collectionDate;
                all_schedules = new ArrayList<>();

                if (number_of_schedules > 0) {
                    for (int i = 0; i < number_of_schedules; i++) {

                        EsusuRepaymentSchedule schedule = new EsusuRepaymentSchedule();
                        Date payableDate = dateUtility.addToDate(schedule_start_date, 1, frequencyId);

                        schedule.setEsusuGroup(group);
                        schedule.setEsusuGroupMembers(group_member);
                        schedule.setInterestAmount(monthlyInterestAmountToPayback);
                        schedule.setMemberProfile((MemberProfile) daoService.getEntity(MemberProfile.class, egm.getMemberProfileId()));
                        schedule.setPaid(Boolean.FALSE);
                        schedule.setPrincipalAmount(group.getContributionAmount());
                        schedule.setRepaymentDate(payableDate);
                        schedule.setTotalAmount(schedule.getPrincipalAmount().add(schedule.getInterestAmount()));
                        all_schedules.add(schedule);
                        schedule_start_date = payableDate;
                    }
                    records_to_create.put(group_member, all_schedules);
                    //logger.info("level {},memberId {}, collection position {}", level, group_member.getMemberProfile().getId(), group_member.getCollectionPosition());
                } else {
                    records_to_create.put(group_member, null);
                }

                level++;
                newStartDate = collectionDate;
            }

        } else {//not with interest
            List<EsusuRepaymentSchedule> all_schedules;

            Date newStartDate = startDate;
            for (EsusuGroupMemberDto egm : dto.getGroupMembers()) {
                Date collectionDate = dateUtility.addToDate(newStartDate, 1, frequencyId);

                if (!repeating_ids.add(egm.getMemberProfileId())) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseInformation("The specified member id [" + egm.getMemberProfileId() + "], is repeating, please remove duplicates."));
                }

                if (!daoService.checkObjectExists(EsusuGroupMembers.class, egm.getId())) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseInformation("The specified esusu group member id [" + egm.getId() + "], does not exist."));
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
                group_member.setExpectedAmount(BigDecimal.valueOf(totalMembers).multiply(group.getMonthlyCollectionAmount()));
                group_member.setAmountPaid(BigDecimal.ZERO);
                group_member.setPaid(Boolean.FALSE);
                group_member.setExpectedCollectionDate(collectionDate);
                group_member.setExpectedAmount(group.getContributionAmount().multiply(BigDecimal.valueOf(totalMembers)));
                group_member.setInterestAmountToReceive(BigDecimal.ZERO);
                group_member.setInterestPaid(Boolean.FALSE);
                group_member.setTotalAmountToReceive(group_member.getExpectedAmount());
                group_member.setMonthlyInterestAmountPayback(BigDecimal.ZERO);
                int number_of_schedules = totalMembers - level;
                group_member.setNumberOfPaybackSchedules(number_of_schedules);
                group_member.setInterestAmountToPayback(BigDecimal.ZERO);
                Date schedule_start_date = collectionDate;
                all_schedules = new ArrayList<>();

                if (number_of_schedules > 0) {
                    for (int i = 0; i < number_of_schedules; i++) {

                        EsusuRepaymentSchedule schedule = new EsusuRepaymentSchedule();
                        Date payableDate = dateUtility.addToDate(schedule_start_date, 1, frequencyId);

                        schedule.setEsusuGroup(group);
                        schedule.setEsusuGroupMembers(group_member);
                        schedule.setInterestAmount(BigDecimal.ZERO);
                        schedule.setMemberProfile((MemberProfile) daoService.getEntity(MemberProfile.class, egm.getMemberProfileId()));
                        schedule.setPaid(Boolean.FALSE);
                        schedule.setPrincipalAmount(group.getContributionAmount());
                        schedule.setRepaymentDate(payableDate);
                        schedule.setTotalAmount(schedule.getPrincipalAmount().add(schedule.getInterestAmount()));
                        all_schedules.add(schedule);
                        schedule_start_date = payableDate;
                    }
                    records_to_create.put(group_member, all_schedules);
                    //logger.info("level {},memberId {}, collection position {}", level, group_member.getMemberProfile().getId(), group_member.getCollectionPosition());
                } else {
                    records_to_create.put(group_member, null);
                }

                level++;
                newStartDate = collectionDate;
            }
        }
        logger.info("total records to create {}", records_to_create.size());
        if (dto.getPreview() != null && dto.getPreview()) {
            ArrangeGroupMemberCollectionPositionWrapperDto wrapperDto = new ArrangeGroupMemberCollectionPositionWrapperDto();
            List<ArrangeGroupMemberCollectionPositionDto> all_records_dto = new ArrayList<>();

            records_to_create.forEach((key, value) -> {
                ArrangeGroupMemberCollectionPositionDto each_record = new ArrangeGroupMemberCollectionPositionDto();
                List<EsusuRepaymentScheduleDto> scheduleListDto = new ArrayList<>();
                EsusuGroupMemberDto groupMemberDto = modelMapping.esusuGroupMemberToDto(key);
                if (value != null) {
                    value.stream().forEach(schedule -> {
                        EsusuRepaymentScheduleDto scheduleDto = modelMapping.EsusuRepaymentScheduleToDto(schedule);
                        scheduleListDto.add(scheduleDto);
                    });
                    each_record.setEsusuGroupMemberDTO(groupMemberDto);
                    each_record.setSchedules(scheduleListDto);
                    all_records_dto.add(each_record);
                } else {
                    each_record.setEsusuGroupMemberDTO(groupMemberDto);
                    all_records_dto.add(each_record);
                }

            });
            //wrapperDto.setRecords(all_records_dto);
            all_records_dto.sort((p1, p2) -> (p1.getEsusuGroupMemberDTO().getCollectionPosition() - p2.getEsusuGroupMemberDTO().getCollectionPosition()));
            return ResponseEntity.ok(all_records_dto);
        }
        group.setPositionArranged(Boolean.TRUE);
        boolean created = memberService.arrangeEsusGroupCollection(records_to_create, group);

        if (!created) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("Unable to persist changes due to error, "
                            + "Please contact Administrator"));
        }
        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    public ResponseEntity<?> viewAllEsusuGroup() throws Exception {

        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<EsusuGroupDTO> list_out = new ArrayList<>();
        //List<EsusuGroup> all_groups = daoService.getAllEntityRecords(EsusuGroup.class);
        logger.info("gone to db");
        List<EsusuGroup> all_groups = memberService.retrieveAllGroups();
        logger.info("back from db with {} ", all_groups.size());
        InterestDisbursementType disburseType_1 = (InterestDisbursementType) daoService.getEntity(InterestDisbursementType.class, 1);
        InterestDisbursementType disburseType_2 = (InterestDisbursementType) daoService.getEntity(InterestDisbursementType.class, 2);
        if (all_groups.isEmpty()) {
            return ResponseEntity.ok(new ResponseInformation("No record found"));
        }

        all_groups.parallelStream().forEach(inv -> {
            EsusuGroupDTO dto = new EsusuGroupDTO();

            dto.setId(inv.getId());
            dto.setContributionAmount(inv.getContributionAmount());
            //dto.setContributionFrequencyId(inv.getContributionFrequency().getId());
            dto.setCircleEnded(inv.getCircleCompleted());
            dto.setCreatedByUsername(inv.getCreatedByUsername());
            dto.setCreationDate(formatter.format(inv.getCreationDate()));
            dto.setDescription(inv.getDescription());
            dto.setEndDate(formatter.format(inv.getEndDate()));
            dto.setInterest_disbursement_type_id(inv.getInterestDisbursementType().getId());
            dto.setName(inv.getName());
            dto.setNumberOfContributors(inv.getNumberOfContributors());
            dto.setStartDate(formatter.format(inv.getStartDate()));
            if (inv.getInterestDisbursementType().getId() == 1) {
                dto.setInterestDisbursementTypeName(disburseType_1.getName());
            } else {
                dto.setInterestDisbursementTypeName(disburseType_2.getName());
            }
            //dto.setContributionFrequencyName(contribFreq.getName());

            list_out.add(dto);
        });

        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewEsusuGroupByCreator() throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<EsusuGroupDTO> list_out = new ArrayList<>();
        List<EsusuGroup> all_groups = daoService.getAllEntityRecords(EsusuGroup.class);
        if (all_groups.isEmpty()) {
            return ResponseEntity.ok(new ResponseInformation("No record found"));
        }

        InterestDisbursementType disburseType_1 = (InterestDisbursementType) daoService.getEntity(InterestDisbursementType.class, 1);
        InterestDisbursementType disburseType_2 = (InterestDisbursementType) daoService.getEntity(InterestDisbursementType.class, 2);

        all_groups.stream().forEach(inv -> {
            EsusuGroupDTO dto = new EsusuGroupDTO();

            dto.setId(inv.getId());
            dto.setContributionAmount(inv.getContributionAmount());
            dto.setContributionFrequencyId(inv.getContributionFrequency().getId());
            dto.setCircleEnded(inv.getCircleCompleted());
            dto.setCreatedByUsername(inv.getCreatedByUsername());
            dto.setCreationDate(formatter.format(inv.getCreationDate()));
            dto.setDescription(inv.getDescription());
            dto.setEndDate(formatter.format(inv.getEndDate()));
            dto.setInterest_disbursement_type_id(inv.getInterestDisbursementType().getId());
            dto.setName(inv.getName());
            dto.setNumberOfContributors(inv.getNumberOfContributors());
            dto.setStartDate(formatter.format(inv.getStartDate()));
            //dto.setContributionFrequencyName(contribFreq.getName());
            if (inv.getInterestDisbursementType().getId() == 1) {
                dto.setInterestDisbursementTypeName(disburseType_1.getName());
            } else {
                dto.setInterestDisbursementTypeName(disburseType_2.getName());
            }
            list_out.add(dto);
        });
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewEsusuGroupById(int esusuGroupId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        if (!daoService.checkObjectExists(EsusuGroup.class, esusuGroupId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified esusu group id is invalid"));
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        EsusuGroup inv = (EsusuGroup) daoService.getEntity(EsusuGroup.class, esusuGroupId);
        InterestDisbursementType disburseType = (InterestDisbursementType) daoService.getEntity(InterestDisbursementType.class, inv.getInterestDisbursementType().getId());
        ContributionFrequency contribFreq = (ContributionFrequency) daoService.getEntity(ContributionFrequency.class, inv.getContributionFrequency().getId());

        EsusuGroupDTO dto = new EsusuGroupDTO();
        dto.setId(inv.getId());
        dto.setContributionAmount(inv.getContributionAmount());
        dto.setContributionFrequencyId(inv.getContributionFrequency().getId());
        dto.setCircleEnded(inv.getCircleCompleted());
        dto.setCreatedByUsername(inv.getCreatedByUsername());
        dto.setCreationDate(formatter.format(inv.getCreationDate()));
        dto.setDescription(inv.getDescription());
        dto.setEndDate(formatter.format(inv.getEndDate()));
        dto.setInterest_disbursement_type_id(inv.getInterestDisbursementType().getId());
        dto.setName(inv.getName());
        dto.setNumberOfContributors(inv.getNumberOfContributors());
        dto.setStartDate(formatter.format(inv.getStartDate()));
        dto.setContributionFrequencyName(contribFreq.getName());
        dto.setInterestDisbursementTypeName(disburseType.getName());

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> viewMemberWallet(int memberProfileId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        if (!daoService.checkObjectExists(MemberProfile.class, memberProfileId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified member profile id is invalid"));
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        MemberProfile profile = (MemberProfile) daoService.getEntity(MemberProfile.class, memberProfileId);
        MemberWallet wallet = memberService.getMemberWallet(memberProfileId);

        MemberWalletDto dto = new MemberWalletDto();
        dto.setId(profile.getId());
        dto.setBalance(wallet.getBalance());
        dto.setMemberName(profile.getName());
        dto.setMemberProfileId(memberProfileId);
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> viewAllContributionFrequency() throws Exception {
        List<ContributionFrequencyDto> list_out = new ArrayList<>();
        List<ContributionFrequency> all_groups = daoService.getAllEntityRecords(ContributionFrequency.class);
        if (all_groups.isEmpty()) {
            return ResponseEntity.ok(new ResponseInformation("No record found"));
        }

        all_groups.stream().forEach(inv -> {
            ContributionFrequencyDto dto = new ContributionFrequencyDto();

            dto.setId(inv.getId());
            dto.setName(inv.getName());
            list_out.add(dto);
        });

        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewAllInterestDisbursementTypes() throws Exception {

        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        List<InterestDisbursementTypeDto> list_out = new ArrayList<>();
        List<InterestDisbursementType> all_groups = daoService.getAllEntityRecords(InterestDisbursementType.class);
        if (all_groups.isEmpty()) {
            return ResponseEntity.ok(new ResponseInformation("No record found"));
        }

        all_groups.stream().forEach(inv -> {
            InterestDisbursementTypeDto dto = new InterestDisbursementTypeDto();

            dto.setId(inv.getId());
            dto.setName(inv.getName());
            list_out.add(dto);
        });

        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewEsusuGroupCollectionArrangement(int esusuGroupId) throws Exception {
        List<EsusuGroupMemberDto> list_out = new ArrayList<>();
        try {
            if (!daoService.checkObjectExists(EsusuGroup.class, esusuGroupId)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified group id is invalid."));
            }
            EsusuGroup esusuGroup = (EsusuGroup) daoService.getEntity(EsusuGroup.class, esusuGroupId);
            if (esusuGroup.getPositionArranged() != null && !esusuGroup.getPositionArranged()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The group collection position has not been prepared."));
            }
            List<EsusuGroupMembers> members = memberService.viewEsusuGroupMembers(esusuGroupId);

            members.stream().forEach(member -> {
                EsusuGroupMemberDto memberDto = modelMapping.esusuGroupMemberToDto(member);
                List<EsusuRepaymentScheduleDto> schedules_dto = new ArrayList<>();
                List<EsusuRepaymentSchedule> schedules_hib = new ArrayList<>();
                try {
                    schedules_hib = memberService.viewGroupMemberRepaymentSchedules(member.getId());
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RequestLogic.class.getName()).log(Level.SEVERE, null, ex);
                }
                schedules_hib.stream().forEach(schedule -> {
                    EsusuRepaymentScheduleDto schedule_dto = modelMapping.EsusuRepaymentScheduleToDto(schedule);
                    schedules_dto.add(schedule_dto);
                });
                memberDto.setSchedules(schedules_dto);
                list_out.add(memberDto);
            });
        } catch (Exception ex) {
            logger.error("An error occured while preparing list. ", ex);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewEsusuGroupMembers(int esusuGroupId) throws Exception {
        List<EsusuGroupMemberDto> list_out = new ArrayList<>();
        try {
            if (!daoService.checkObjectExists(EsusuGroup.class, esusuGroupId)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified group id is invalid."));
            }
            EsusuGroup esusuGroup = (EsusuGroup) daoService.getEntity(EsusuGroup.class, esusuGroupId);

            List<EsusuGroupMembers> members = memberService.viewEsusuGroupMembers(esusuGroupId);
            if (members.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("No record found."));
            }

            members.stream().forEach(member -> {
                try {
                    EsusuGroupMemberDto memberDto = modelMapping.esusuGroupMemberToDto(member);
                    if (memberService.checkMemberHasProfilePicture(member.getId())) {
                        MemberProfilePicture picture = memberService.getMemberProfilePicture(member.getId());
                        File file = new File(picture.getPicturepath());
                        byte[] read = Files.readAllBytes(file.toPath());
                        String encodedContents = converter.encodeToString(read);

                        MemberProfilePictureDTO dto_pic = new MemberProfilePictureDTO();
                        //dto.setFileName(picture.getPicturepath());
                        dto_pic.setFileContent(encodedContents);
                        dto_pic.setMemberProfileId(member.getId());
                        dto_pic.setMemberName(memberDto.getMemberName());
                        memberDto.setProfilePicture(dto_pic);
                    }
                    list_out.add(memberDto);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RequestLogic.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception ex) {
            logger.error("An error occured while preparing list. ", ex);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> buildDatabaseIndex() throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        boolean done = memberService.buildDatabaseIndex();
        if (!done) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }

        return ResponseEntity.ok(new ResponseInformation("Successful"));
    }

    public ResponseEntity<?> uploadMemberProfilePicture(MultipartFile file, Integer memberId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        if (!daoService.checkObjectExists(MemberProfile.class, memberId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified member id for upload of documents is not valid"));
        }
        MemberProfile memberProfile = (MemberProfile) daoService.getEntity(MemberProfile.class, memberId);

        if (!Objects.equals(initiator.getId(), memberId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("You are not allowed to performed this operation on behalf of another member."));
        }

        if (memberProfile.getAccountClosed() != null && memberProfile.getAccountClosed()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("This account has been closed."));
        }

        MemberProfilePicture picture;
        boolean hasPicture = memberService.checkMemberHasProfilePicture(memberId);

        if (hasPicture) {
            picture = memberService.getMemberProfilePicture(memberId);
            File file_old = new File(picture.getPicturepath());
            if (file_old.exists()) {
                file_old.delete();

                String fileName = pictureFileStorageService.storeFile(file);
                picture.setMemberProfile(memberProfile);
                picture.setPicturepath(fileName);

            }

        } else {
            picture = new MemberProfilePicture();

            String fileName = pictureFileStorageService.storeFile(file);
            logger.info("file path {} ", fileName);
            picture.setMemberProfile(memberProfile);
            picture.setPicturepath(fileName);
        }

        boolean updated = daoService.saveUpdateEntity(picture);

        if (!updated) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The database could not make the necessary change. "
                            + "Please contact Administrator"));
        }

        return ResponseEntity.ok(new ResponseInformation("successful"));
    }

    public ResponseEntity<?> viewMemberProfilePicture(Integer memberId) throws Exception {
        //MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        if (!daoService.checkObjectExists(MemberProfile.class, memberId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified member id is not valid"));
        }

        if (!memberService.checkMemberHasProfilePicture(memberId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("You currently do not have a profile picture"));
        }

        MemberProfilePicture picture = memberService.getMemberProfilePicture(memberId);
        File file = new File(picture.getPicturepath());
        byte[] read = Files.readAllBytes(file.toPath());
        String encodedContents = converter.encodeToString(read);

        MemberProfile memberProfile = (MemberProfile) daoService.getEntity(MemberProfile.class, memberId);

        MemberProfilePictureDTO dto = new MemberProfilePictureDTO();
        //dto.setFileName(picture.getPicturepath());
        dto.setFileContent(encodedContents);
        dto.setMemberProfileId(memberId);
        dto.setMemberName(memberProfile.getName());

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> getMemberBalances(int memberProfileId) throws Exception {
        //coop admin authentication logic starts here
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        if (!daoService.checkObjectExists(MemberProfile.class, memberProfileId)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseInformation("The specified member does not exist. "
                            + "You cannot carry out this function"));
        }
        MemberProfile memberProfile = (MemberProfile) daoService.getEntity(MemberProfile.class, memberProfileId);

        BigDecimal compulsoryBalance = memberService.getMemberWalletBalance(memberProfileId);
        BalanceDTO dto = new BalanceDTO();
        if (compulsoryBalance == null) {
            compulsoryBalance = new BigDecimal(0.0);
        }
        dto.setWalletBalance(compulsoryBalance);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> viewEsusuGroupCollectors(int esusuGroupId) throws Exception {
        List<EsusuGroupMemberDto> list_out = new ArrayList<>();
        try {
            if (!daoService.checkObjectExists(EsusuGroup.class, esusuGroupId)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified group id is invalid."));
            }
            EsusuGroup esusuGroup = (EsusuGroup) daoService.getEntity(EsusuGroup.class, esusuGroupId);
            if (esusuGroup.getPositionArranged() != null && !esusuGroup.getPositionArranged()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The group collection position has not been prepared."));
            }
            List<EsusuGroupMembers> members = memberService.viewEsusuGroupCollectors(esusuGroupId);

            members.stream().forEach(member -> {
                EsusuGroupMemberDto memberDto = modelMapping.esusuGroupMemberToDto(member);
                List<EsusuRepaymentScheduleDto> schedules_dto = new ArrayList<>();
                List<EsusuRepaymentSchedule> schedules_hib = new ArrayList<>();
                try {
                    if (memberService.checkMemberHasProfilePicture(member.getId())) {
                        MemberProfilePicture picture = memberService.getMemberProfilePicture(member.getId());
                        File file = new File(picture.getPicturepath());
                        byte[] read = Files.readAllBytes(file.toPath());
                        String encodedContents = converter.encodeToString(read);

                        MemberProfilePictureDTO dto_pic = new MemberProfilePictureDTO();
                        //dto.setFileName(picture.getPicturepath());
                        dto_pic.setFileContent(encodedContents);
                        dto_pic.setMemberProfileId(member.getId());
                        dto_pic.setMemberName(memberDto.getMemberName());
                        memberDto.setProfilePicture(dto_pic);
                    }
                    schedules_hib = memberService.viewGroupMemberRepaymentSchedules(member.getId());
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RequestLogic.class.getName()).log(Level.SEVERE, null, ex);
                }
                schedules_hib.stream().forEach(schedule -> {
                    EsusuRepaymentScheduleDto schedule_dto = modelMapping.EsusuRepaymentScheduleToDto(schedule);
                    schedules_dto.add(schedule_dto);
                });
                memberDto.setSchedules(schedules_dto);
                list_out.add(memberDto);
            });
        } catch (Exception ex) {
            logger.error("An error occured while preparing list. ", ex);
        }
        return ResponseEntity.ok(list_out);
    }

    public ResponseEntity<?> viewUserEsusuGroups(int memberProfileId) throws Exception {
        MemberProfile initiator = memberService.getUserInformation(authenticationFacade.getAuthentication().getName());
        List<EsusuGroupMemberDto> list_out = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd");
        try {
            if (!daoService.checkObjectExists(MemberProfile.class, memberProfileId)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("The specified member id is invalid."));
            }
            if (initiator.getId() != memberProfileId) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("You are not authorised to see this information"));
            }
            //EsusuGroup esusuGroup = (EsusuGroup) daoService.getEntity(EsusuGroup.class, esusuGroupId);
            List<EsusuGroupMembers> members = memberService.viewUserEsusuGroups(memberProfileId);

            if (members.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseInformation("You are currently not in any group."));
            }

            members.stream().forEach(member -> {
                EsusuGroupMemberDto memberDto = modelMapping.esusuGroupMemberToDto(member);
                try {
                    EsusuGroup group_hib = (EsusuGroup) daoService.getEntity(EsusuGroup.class, member.getEsusuGroup().getId());

                    EsusuGroupDTO dto_esu = new EsusuGroupDTO();

                    dto_esu.setId(group_hib.getId());
                    dto_esu.setContributionAmount(group_hib.getContributionAmount());
                    dto_esu.setContributionFrequencyId(group_hib.getContributionFrequency().getId());
                    dto_esu.setCircleEnded(group_hib.getCircleCompleted());
                    dto_esu.setCreatedByUsername(group_hib.getCreatedByUsername());
                    dto_esu.setCreationDate(formatter.format(group_hib.getCreationDate()));
                    dto_esu.setDescription(group_hib.getDescription());
                    dto_esu.setEndDate(formatter.format(group_hib.getEndDate()));
                    dto_esu.setInterest_disbursement_type_id(group_hib.getInterestDisbursementType().getId());
                    dto_esu.setName(group_hib.getName());
                    dto_esu.setNumberOfContributors(group_hib.getNumberOfContributors());
                    dto_esu.setStartDate(formatter.format(group_hib.getStartDate()));
                    memberDto.setEsusuGroup(dto_esu);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RequestLogic.class.getName()).log(Level.SEVERE, null, ex);
                }
                List<EsusuRepaymentScheduleDto> schedules_dto = new ArrayList<>();
                List<EsusuRepaymentSchedule> schedules_hib = new ArrayList<>();
                try {
                    schedules_hib = memberService.viewGroupMemberRepaymentSchedules(member.getId());
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RequestLogic.class.getName()).log(Level.SEVERE, null, ex);
                }
                schedules_hib.stream().forEach(schedule -> {
                    EsusuRepaymentScheduleDto schedule_dto = modelMapping.EsusuRepaymentScheduleToDto(schedule);
                    schedules_dto.add(schedule_dto);
                });
                memberDto.setSchedules(schedules_dto);
                list_out.add(memberDto);
            });
        } catch (Exception ex) {
            logger.error("An error occured while preparing list. ", ex);
        }
        return ResponseEntity.ok(list_out);
    }
    
    public ResponseEntity<?> searchForMembers(String searchTerm, Integer pageNumber, Integer pageSize) throws Exception {

        List<MemberProfileDTO> memberDTOs = new ArrayList<>();
        List<MemberProfile> members = memberService.searchForMember(searchTerm, pageNumber, pageSize);
        for (MemberProfile member : members) {
            MemberProfileDTO memberDTO = modelMapping.memberToDtoMapping(member);
            memberDTOs.add(memberDTO);
        }

        return ResponseEntity.ok(memberDTOs);
    }
}
