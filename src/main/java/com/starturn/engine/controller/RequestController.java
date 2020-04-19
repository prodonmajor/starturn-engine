/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller;

import com.starturn.engine.controller.businesslogic.RequestLogic;
import com.starturn.engine.models.ChangePasswordDTO;
import com.starturn.engine.models.EsusuGroupDTO;
import com.starturn.engine.models.EsusuGroupInviteWrapper;
import com.starturn.engine.models.EsusuGroupInvitesDTO;
import com.starturn.engine.models.EsusuGroupMembersWrapperDTO;
import com.starturn.engine.models.MemberProfileDTO;
import com.starturn.engine.models.response.ResponseInformation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/starturn/v1/api")
@Api(value = "Request API calls")
public class RequestController {

    private @Autowired
    RequestLogic logic;

    @PostMapping("/changepassword")
    @ApiOperation(value = "Change password request")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO passwordInfo, BindingResult result) throws Exception {
        return logic.changePassword(passwordInfo, result);
    }

    @PostMapping("/creategroup")
    @ApiOperation(value = "create a new esusu group")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> createGroup(@Valid @RequestBody EsusuGroupDTO dto, BindingResult result) throws Exception {
        return logic.createGroup(dto, result);
    }

    @PostMapping("/inviteuserstoesusugroup")
    @ApiOperation(value = "invite users to join group")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> inviteUsersToJoinGroup(@Valid @RequestBody EsusuGroupInviteWrapper dto, BindingResult result) {
        return logic.inviteUsersToJoinGroup(dto, result);
    }

    @GetMapping("/login")
    @ApiOperation(value = "User login")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = MemberProfileDTO.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> login() throws Exception {
        return logic.login();
    }

    @GetMapping("/resetpassword")
    @ApiOperation(value = "Reset password")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> resetPassword(@RequestParam(value = "username", required = true) String username) throws Exception {
        return logic.resetPassword(username);
    }

    @PostMapping("/acceptorrejectinvitetojoingroup")
    @ApiOperation(value = "treat group invitation request")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> treatGroupInvitation(@RequestParam(value = "invitationid", required = true) Integer invitationId, @RequestParam(value = "status", required = true) Boolean status) throws Exception {
        return logic.treatGroupInvitation(invitationId, status);
    }

    @PostMapping("/updatememberprofile")
    @ApiOperation(value = "update a user's profile")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> updateMemberProfile(@Valid @RequestBody MemberProfileDTO dto, BindingResult result) throws Exception {
        return logic.updateMemberProfile(dto, result);
    }

    @GetMapping("/viewacceptedgroupinvitations")
    @ApiOperation(value = "view accepted group invitation request")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = EsusuGroupInvitesDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> viewAcceptedGroupInvitations(@RequestParam(value = "groupid", required = true) Integer groupId) throws Exception {
        return logic.viewAcceptedGroupInvitations(groupId);
    }

    @GetMapping("/viewrejectedgroupinvitations")
    @ApiOperation(value = "view rejected group invitation request")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = EsusuGroupInvitesDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> viewRejectedGroupInvitations(@RequestParam(value = "groupid", required = true) Integer groupId) throws Exception {
        return logic.viewRejectedGroupInvitations(groupId);
    }

    @GetMapping("/viewusergroupinvitations")
    @ApiOperation(value = "view user group invitation requests")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = EsusuGroupInvitesDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> viewMemberGroupInvitations(@RequestParam(value = "memberid", required = true) Integer memberProfileId) throws Exception {
        return logic.viewMemberGroupInvitations(memberProfileId);
    }

    @GetMapping("/viewgroupinvitations")
    @ApiOperation(value = "view group invitation requests")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = EsusuGroupInvitesDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> viewGroupInvitations(@RequestParam(value = "groupid", required = true) Integer groupId) throws Exception {
        return logic.viewGroupInvitations(groupId);
    }

    @GetMapping("/viewuserdetails")
    @ApiOperation(value = "view user details")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = MemberProfileDTO.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> viewUserDetails(@RequestParam(value = "memberid", required = true) Integer profileId) throws Exception {
        return logic.viewUserDetails(profileId);
    }

    @PostMapping("/preparegroupsavingscollectiondate")
    @ApiOperation(value = "prepare group savings collection date")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> prepareGroupSavingsCollectionDate(@Valid @RequestBody EsusuGroupMembersWrapperDTO dto, BindingResult result) throws Exception{
    return logic.prepareGroupSavingsCollectionDate(dto, result);
    }
    
    @GetMapping("/accepttermsandconditions")
    @ApiOperation(value = "Extension requirement")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful", response = ResponseInformation.class),
        @ApiResponse(code = 400, message = "incorrect information provided", response = ResponseInformation.class),
        @ApiResponse(code = 500, message = "internal error from database or other system functions - critical!", response = ResponseInformation.class)
    })
    public ResponseEntity<?> acceptTermsAndConditions() throws Exception {
        return logic.acceptTermsAndConditions();
    }
}
