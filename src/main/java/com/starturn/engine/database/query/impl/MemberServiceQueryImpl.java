/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.query.impl;

import com.starturn.engine.database.entities.EsusuGroup;
import com.starturn.engine.database.entities.EsusuGroupInvites;
import com.starturn.engine.database.entities.EsusuGroupMembers;
import com.starturn.engine.database.entities.EsusuRepaymentSchedule;
import com.starturn.engine.database.entities.InterestDisbursementType;
import com.starturn.engine.database.entities.MemberProfile;
import com.starturn.engine.database.entities.MemberProfilePicture;
import com.starturn.engine.database.entities.MemberWallet;
import com.starturn.engine.database.entities.MemberWalletTransaction;
import com.starturn.engine.database.entities.UserToken;
import com.starturn.engine.database.query.MemberServiceQuery;
import com.starturn.engine.database.util.CustomIndexerProgressMonitor;
import com.starturn.engine.database.util.HibernateDataAccess;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.query.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

/**
 *
 * @author Administrator
 */
public class MemberServiceQueryImpl implements MemberServiceQuery {

    private static final Logger logger = LogManager.getLogger(MemberServiceQueryImpl.class);
    EntityManager em;

    @Override
    public boolean checkUserExists(String username) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        Long count = 0L;
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<Long> cr = cb.createQuery(Long.class);

            Root<MemberProfile> root = cr.from(MemberProfile.class);
            cr.select(cb.count(root)).where(cb.equal(root.get("username"), username));

            Query<Long> query = dao.getSession().createQuery(cr);
            count = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return count > 0;
    }

    @Override
    public MemberProfile getUserInformation(String username) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        MemberProfile profile = new MemberProfile();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<MemberProfile> cr = cb.createQuery(MemberProfile.class);

            Root<MemberProfile> root = cr.from(MemberProfile.class);
            cr.select(root).where(cb.equal(root.get("username"), username));

            Query<MemberProfile> query = dao.getSession().createQuery(cr);
            profile = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return profile;
    }

    @Override
    public boolean checkPhoneNumberExists(String phoneNumber) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        Long count = 0L;
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<Long> cr = cb.createQuery(Long.class);

            Root<MemberProfile> root = cr.from(MemberProfile.class);
            cr.select(cb.count(root)).where(cb.equal(root.get("phoneNumber"), phoneNumber));

            Query<Long> query = dao.getSession().createQuery(cr);
            count = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return count > 0;
    }

    @Override
    public boolean userSignUp(MemberProfile profile, UserToken token) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean saved = false;
        try {
            dao.startOperation();

            dao.createUpdateObject(profile);

            token.setMemberProfile(profile);
            dao.createUpdateObject(token);

            dao.commit();
            saved = true;
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return saved;
    }

    @Override
    public boolean checkTokenExists(String token) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        Long count = 0L;
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<Long> cr = cb.createQuery(Long.class);

            Root<UserToken> root = cr.from(UserToken.class);
            cr.select(cb.count(root)).where(cb.equal(root.get("token"), token));

            Query<Long> query = dao.getSession().createQuery(cr);
            count = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return count > 0;
    }

    @Override
    public UserToken retrieveToken(String token) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        UserToken profile = new UserToken();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<UserToken> cr = cb.createQuery(UserToken.class);

            Root<UserToken> root = cr.from(UserToken.class);
            cr.select(root).where(cb.equal(root.get("token"), token));

            Query<UserToken> query = dao.getSession().createQuery(cr);
            profile = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return profile;
    }

    @Override
    public List<EsusuGroupInvites> viewMemberGroupInvitation(int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupInvites> invites = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupInvites> cr = cb.createQuery(EsusuGroupInvites.class);

            Root<EsusuGroupInvites> root = cr.from(EsusuGroupInvites.class);
            Join<EsusuGroupInvites, MemberProfile> member_join = root.join("memberProfile");
            cr.select(root).where(cb.equal(member_join.get("id"), memberProfileId));

            Query<EsusuGroupInvites> query = dao.getSession().createQuery(cr);
            invites = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return invites;
    }

    @Override
    public List<EsusuGroupInvites> viewGroupInvitations(int groupId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupInvites> invites = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupInvites> cr = cb.createQuery(EsusuGroupInvites.class);

            Root<EsusuGroupInvites> root = cr.from(EsusuGroupInvites.class);
            Join<EsusuGroupInvites, EsusuGroup> group_join = root.join("esusuGroup");
            cr.select(root).where(cb.equal(group_join.get("id"), groupId));

            Query<EsusuGroupInvites> query = dao.getSession().createQuery(cr);
            invites = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return invites;
    }

    @Override
    public boolean checkMemberHasGroupInvitation(int groupId, int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        Long count = 0L;
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<Long> cr = cb.createQuery(Long.class);

            Root<EsusuGroupInvites> root = cr.from(EsusuGroupInvites.class);
            Join<EsusuGroupInvites, MemberProfile> member_join = root.join("memberProfile");
            cr.select(cb.count(root)).where(cb.equal(root.get("id"), groupId),
                    cb.equal(member_join.get("id"), memberProfileId));

            Query<Long> query = dao.getSession().createQuery(cr);
            count = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return count > 0;
    }

    @Override
    public List<UserToken> retrieveValidTokens() throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<UserToken> tokens = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<UserToken> cr = cb.createQuery(UserToken.class);

            Root<UserToken> root = cr.from(UserToken.class);

            cr.select(root).where(cb.equal(root.get("validated"), false));

            Query<UserToken> query = dao.getSession().createQuery(cr);
            tokens = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return tokens;
    }

    @Override
    public List<EsusuGroupInvites> viewAllAcceptedGroupInvitations(int groupId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupInvites> invites = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupInvites> cr = cb.createQuery(EsusuGroupInvites.class);

            Root<EsusuGroupInvites> root = cr.from(EsusuGroupInvites.class);
            Join<EsusuGroupInvites, EsusuGroup> group_join = root.join("esusuGroup");
            cr.select(root).where(cb.equal(group_join.get("id"), groupId),
                    cb.equal(root.get("accepted"), true),
                    cb.equal(root.get("rejected"), false));

            Query<EsusuGroupInvites> query = dao.getSession().createQuery(cr);
            invites = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return invites;
    }

    @Override
    public List<EsusuGroupInvites> viewAllRejectedGroupInvitations(int groupId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupInvites> invites = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupInvites> cr = cb.createQuery(EsusuGroupInvites.class);

            Root<EsusuGroupInvites> root = cr.from(EsusuGroupInvites.class);
            Join<EsusuGroupInvites, EsusuGroup> group_join = root.join("esusuGroup");
            cr.select(root).where(cb.equal(group_join.get("id"), groupId),
                    cb.equal(root.get("accepted"), false),
                    cb.equal(root.get("rejected"), true));

            Query<EsusuGroupInvites> query = dao.getSession().createQuery(cr);
            invites = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return invites;
    }

    @Override
    public UserToken retrieveTokenByEmail(String email) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        UserToken profile = new UserToken();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<UserToken> cr = cb.createQuery(UserToken.class);

            Root<UserToken> root = cr.from(UserToken.class);
            cr.select(root).where(cb.equal(root.get("username"), email));

            Query<UserToken> query = dao.getSession().createQuery(cr);
            profile = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return profile;
    }

    @Override
    public boolean arrangeEsusGroupCollection(Map<EsusuGroupMembers, List<EsusuRepaymentSchedule>> records, EsusuGroup group) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean applied = false;
        try {
            dao.startOperation();
            records.forEach((key, value) -> {
                dao.createUpdateObject(key);
                if (value != null) {
                    value.forEach(repaymentSchedule -> {
                        dao.createUpdateObject(repaymentSchedule);
                    });
                }

            });
            dao.createUpdateObject(group);
            dao.commit();
            applied = true;
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return applied;
    }

    @Override
    public List<EsusuGroup> viewEsusuGroupByCreator(String creatorUsername) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroup> invites = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroup> cr = cb.createQuery(EsusuGroup.class);

            Root<EsusuGroup> root = cr.from(EsusuGroup.class);

            cr.select(root).where(cb.equal(root.get("createdByUsername"), creatorUsername));

            Query<EsusuGroup> query = dao.getSession().createQuery(cr);
            invites = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return invites;
    }

    @Override
    public MemberWallet getMemberWallet(int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        MemberWallet wallet;
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<MemberWallet> cr = cb.createQuery(MemberWallet.class);

            Root<MemberWallet> root = cr.from(MemberWallet.class);
            Join<MemberWallet, MemberProfile> member_join = root.join("memberProfile");
            cr.select(root).where(cb.equal(member_join.get("id"), memberProfileId));

            Query<MemberWallet> query = dao.getSession().createQuery(cr);
            wallet = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return wallet;
    }

    @Override
    public List<EsusuGroupMembers> viewEsusuGroupMembers(int groupId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupMembers> groupmembers = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupMembers> cr = cb.createQuery(EsusuGroupMembers.class);

            Root<EsusuGroupMembers> root = cr.from(EsusuGroupMembers.class);
            Join<EsusuGroupMembers, EsusuGroup> group_join = root.join("esusuGroup");
            cr.select(root).where(cb.equal(group_join.get("id"), groupId));

            Query<EsusuGroupMembers> query = dao.getSession().createQuery(cr);
            groupmembers = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return groupmembers;
    }

    @Override
    public List<EsusuRepaymentSchedule> viewGroupMemberRepaymentSchedules(int esusuGroupMemberId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuRepaymentSchedule> repaymentSchedules = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuRepaymentSchedule> cr = cb.createQuery(EsusuRepaymentSchedule.class);

            Root<EsusuRepaymentSchedule> root = cr.from(EsusuRepaymentSchedule.class);
            Join<EsusuRepaymentSchedule, EsusuGroupMembers> group_join = root.join("esusuGroupMembers");
            cr.select(root).where(cb.equal(group_join.get("id"), esusuGroupMemberId));

            Query<EsusuRepaymentSchedule> query = dao.getSession().createQuery(cr);
            repaymentSchedules = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return repaymentSchedules;
    }

    @Override
    public List<EsusuGroup> retrieveAllGroups() throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroup> invites = new ArrayList<>(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<Tuple> cr = cb.createTupleQuery();

            Root<EsusuGroup> root = cr.from(EsusuGroup.class);

            cr.multiselect(
                    root.get("id"),
                    root.get("name"),
                    root.get("description"),
                    root.get("contributionAmount"),
                    root.get("numberOfContributors"),
                    root.get("startDate"),
                    root.get("endDate"),
                    root.get("createdByUsername"),
                    root.get("circleCompleted"),
                    root.get("isWithInterest"),
                    root.get("positionArranged"),
                    root.get("monthlyCollectionAmount"),
                    root.get("creationDate"),
                    root.get("interestDisbursementType")
            );
            cr.orderBy(cb.desc(root.get("creationDate")));

            Query<Tuple> query = dao.getSession().createQuery(cr);
            List<Tuple> tupleResult = query.getResultList();
            for (Tuple t : tupleResult) {
                EsusuGroup group = new EsusuGroup();
                group.setId((int) t.get(0));
                group.setName((String) t.get(1));
                group.setDescription((String) t.get(2));
                group.setContributionAmount((BigDecimal) t.get(3));
                group.setNumberOfContributors((Integer)t.get(4));
                group.setStartDate((Date)t.get(5));
                group.setEndDate((Date)t.get(6));
                group.setCreatedByUsername((String)t.get(7));
                group.setCircleCompleted((Boolean) t.get(8));
                group.setIsWithInterest((Boolean) t.get(9));
                group.setPositionArranged((Boolean) t.get(10));
                group.setMonthlyCollectionAmount((BigDecimal) t.get(11));
                group.setCreationDate((Date) t.get(12));
                group.setInterestDisbursementType((InterestDisbursementType) t.get(13));
                invites.add(group);
            }
            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return invites;
    }
    
    @Override
    public boolean buildDatabaseIndex() throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean done = false;
        try {
            dao.startOperation();

            FullTextSession fullTextSession = Search.getFullTextSession(dao.getSession());
            fullTextSession
                    .createIndexer()
                    .typesToIndexInParallel(2)
                    .batchSizeToLoadObjects(2000) //sql server cannot go beyond 2100 
                    .cacheMode(CacheMode.IGNORE)
                    .threadsToLoadObjects(20)
                    .idFetchSize(150)
                    .progressMonitor(new CustomIndexerProgressMonitor())
                    .startAndWait();

            dao.commit();
            done = true;
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return done;
    }
    
    @Override
    public boolean checkMemberHasProfilePicture(int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        Long count = 0l;
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<Long> cr = cb.createQuery(Long.class);

            Root<MemberProfilePicture> root = cr.from(MemberProfilePicture.class);
            Join<MemberProfilePicture, MemberProfile> member_join = root.join("memberProfile");
            cr.select(cb.count(root)).where(cb.equal(member_join.get("id"), memberProfileId));

            Query<Long> query = dao.getSession().createQuery(cr);
            count = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return count > 0;
    }

    @Override
    public MemberProfilePicture getMemberProfilePicture(int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        MemberProfilePicture member = new MemberProfilePicture();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<MemberProfilePicture> cr = cb.createQuery(MemberProfilePicture.class);

            Root<MemberProfilePicture> root = cr.from(MemberProfilePicture.class);
            Join<MemberProfilePicture, MemberProfile> member_join = root.join("memberProfile");
            cr.select(root).where(cb.equal(member_join.get("id"), memberProfileId));

            Query<MemberProfilePicture> query = dao.getSession().createQuery(cr);
            member = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return member;
    }
    
    @Override
    public BigDecimal getMemberWalletBalance(int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        BigDecimal balance = new BigDecimal(0);
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cr = cb.createQuery(BigDecimal.class);

            Root<MemberWalletTransaction> root = cr.from(MemberWalletTransaction.class);
            Join<MemberWalletTransaction, MemberProfile> member_join = root.join("memberProfile");
            cr.select(cb.sum(root.get("amount"))).where(
                    cb.equal(member_join.get("id"), memberProfileId)
            );

            Query<BigDecimal> query = dao.getSession().createQuery(cr);
            balance = query.getSingleResult();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return balance;
    }

    @Override
    public List<EsusuGroupMembers> viewEsusuGroupCollectors(int groupId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupMembers> groupmembers = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupMembers> cr = cb.createQuery(EsusuGroupMembers.class);

            Root<EsusuGroupMembers> root = cr.from(EsusuGroupMembers.class);
            Join<EsusuGroupMembers, EsusuGroup> group_join = root.join("esusuGroup");
            cr.select(root).where(cb.equal(group_join.get("id"), groupId),
                    cb.equal(root.get("paid"), true));

            Query<EsusuGroupMembers> query = dao.getSession().createQuery(cr);
            groupmembers = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return groupmembers;
    }

    @Override
    public List<EsusuGroupMembers> viewUserEsusuGroups(int memberProfileId) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<EsusuGroupMembers> groupmembers = new ArrayList<>();
        try {
            dao.startOperation();
            CriteriaBuilder cb = dao.getSession().getCriteriaBuilder();
            CriteriaQuery<EsusuGroupMembers> cr = cb.createQuery(EsusuGroupMembers.class);

            Root<EsusuGroupMembers> root = cr.from(EsusuGroupMembers.class);
            Join<EsusuGroupMembers, MemberProfile> memberProfile_join = root.join("memberProfile");
            cr.select(root).where(cb.equal(memberProfile_join.get("id"), memberProfileId));

            Query<EsusuGroupMembers> query = dao.getSession().createQuery(cr);
            groupmembers = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return groupmembers;
    }

    @Override
    public List<MemberProfile> searchForMember(String memberSearchTerm, int pageNumber, int pageSize) throws Exception {
                HibernateDataAccess dao = new HibernateDataAccess();
        List<MemberProfile> members = new ArrayList<>();
        try {
            dao.startOperation();

            FullTextSession fullTextSession = Search.getFullTextSession(dao.getSession());
            QueryBuilder qb = fullTextSession.getSearchFactory()
                    .buildQueryBuilder().forEntity(MemberProfile.class).get();

//            org.apache.lucene.search.Query cooperativeQuery = qb
//                    .keyword()
//                    .onField("cooperative.id")
//                    .matching(cooperativeId)
//                    .createQuery();

            org.apache.lucene.search.Query memberQuery = qb
                    .keyword()
                    .onFields("username", "name", "emailAddress","phoneNumber")
                    .matching(memberSearchTerm + "*")
                    .createQuery();

//            org.apache.lucene.search.Query luceneQuery = qb.bool()
//                    .must(cooperativeQuery)
//                    .must(memberQuery)
//                    .createQuery();

            Query query = fullTextSession.createFullTextQuery(memberQuery, MemberProfile.class)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize);
            members = query.getResultList();

            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return members;
    }
}
