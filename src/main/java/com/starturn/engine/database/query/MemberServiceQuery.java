/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.query;

import com.starturn.engine.database.entities.EsusuGroup;
import com.starturn.engine.database.entities.EsusuGroupInvites;
import com.starturn.engine.database.entities.EsusuGroupMembers;
import com.starturn.engine.database.entities.EsusuRepaymentSchedule;
import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.entities.MemberProfilePicture;
import com.starturn.engine.database.entities.MemberWallet;
import com.starturn.engine.database.entities.Transaction;
import com.starturn.engine.database.entities.UserToken;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    public List<EsusuGroup> viewEsusuGroupByCreator(String creatorUsername) throws Exception;
    public boolean arrangeEsusGroupCollection(Map<EsusuGroupMembers, List<EsusuRepaymentSchedule>> records, EsusuGroup group) throws Exception;
    public MemberWallet getMemberWallet(int memberProfileId) throws Exception;
    public List<EsusuGroupMembers> viewEsusuGroupMembers(int groupId) throws Exception;
    public List<EsusuGroupMembers> viewEsusuGroupCollectors(int groupId) throws Exception;
    public List<EsusuGroupMembers> viewUserEsusuGroups(int memberProfileId) throws Exception;
    public List<EsusuRepaymentSchedule> viewGroupMemberRepaymentSchedules(int esusuGroupMemberId) throws Exception;
    public List<EsusuGroup> retrieveAllGroups() throws Exception;
    public boolean buildDatabaseIndex() throws Exception;
    public boolean checkMemberHasProfilePicture(int memberProfileId) throws Exception;

    public MemberProfilePicture getMemberProfilePicture(int memberProfileId) throws Exception;
    public BigDecimal getMemberWalletBalance(int memberProfileId) throws Exception;
    public List<MemberProfile> searchForMember(String memberSearchTerm, int pageNumber, int pageSize) throws Exception;
    public boolean captureMemberMonthlyContributionCardPayment(Transaction trans, MemberWallet wallet) throws Exception;
    public List<Transaction> viewUserTransactions(int memberProfileId) throws Exception;
    public boolean debitGroupMemberWallet(int walletId, BigDecimal amount) throws Exception;
    public boolean checkMemberHasWallet(int memberProfileId) throws Exception;
    public Transaction getMemberLastTransaction(int memberProfileId) throws Exception;
}
