/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.query;

import com.starturn.engine.database.entities.EsusuGroupInvites;
import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.entities.UserToken;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface MemberServiceQuery {

    /**
     * Checks if a user exists.
     *
     * @param username the username
     * @return true, if username exists
     * @throws Exception
     */
    public boolean checkUserExists(String username) throws Exception;

    /**
     * Gets the member profile for the specified username.
     *
     * @param username the username
     * @return the member profile of the username
     * @throws Exception
     */
    public MemberProfile getUserInformation(String username) throws Exception;

    /**
     * Checks if a phone number has been registered already.
     *
     * @param phoneNumber the phone number
     * @return true, if phone number exists
     * @throws Exception
     */
    public boolean checkPhoneNumberExists(String phoneNumber) throws Exception;

    /**
     * registers a new user
     *
     * @param profile the user details
     * @param token the user account activation token
     * @return true, if sign up is successful
     * @throws Exception
     */
    public boolean userSignUp(MemberProfile profile, UserToken token) throws Exception;

    /**
     * Checks if a token exists.
     *
     * @param token the generated token
     * @return true, if token exists
     * @throws Exception
     */
    public boolean checkTokenExists(String token) throws Exception;

    /**
     * retrieves token
     *
     * @param token the generated token
     * @return true, if token exists
     * @throws Exception
     */
    public UserToken retrieveToken(String token) throws Exception;

    public boolean checkMemberHasGroupInvitation(int groupId, int memberProfileId) throws Exception;

    public List<EsusuGroupInvites> viewMemberGroupInvitation(int memberProfileId) throws Exception;

    public List<EsusuGroupInvites> viewGroupInvitations(int groupId) throws Exception;
    public List<EsusuGroupInvites> viewAllAcceptedGroupInvitations(int groupId) throws Exception;
    public List<EsusuGroupInvites> viewAllRejectedGroupInvitations(int groupId) throws Exception;
    public List<UserToken> retrieveValidTokens() throws Exception;
    public UserToken retrieveTokenByEmail(String email) throws Exception;
}
