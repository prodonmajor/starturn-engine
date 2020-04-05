/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.query.impl;

import com.starturn.engine.database.query.DaoServiceQuery;
import com.starturn.engine.database.util.HibernateDataAccess;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DaoServiceQueryImpl implements DaoServiceQuery {

    private static final Logger logger = LogManager.getLogger(DaoServiceQueryImpl.class);

    @Override
    public boolean saveUpdateEntity(Object object) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean saved = false;
        try {
            dao.startOperation();
            dao.createUpdateObject(object);

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
    public boolean saveUpdateEntities(List<? extends Object> objects) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean saved = false;
        int batchSize = 50;
        try {
            dao.startOperation();

            int count = 0;
            for (Object obj : objects) {
                dao.createUpdateObject(obj);
                if (++count % batchSize == 0) {
                    dao.getSession().flush();
                    dao.getSession().clear();
                }
            }

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
    public Object getEntity(Class clz, Serializable id) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        Object obj = new Object();
        try {
            dao.startOperation();
            obj = dao.searchObject(clz, id);
            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return obj;
    }

    @Override
    public List getAllEntityRecords(Class clz) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        List<? extends Object> obj = new ArrayList<>();
        try {
            dao.startOperation();
            obj = dao.searchAll(clz);
            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return obj;
    }

    @Override
    public boolean checkObjectExists(Class clz, Serializable id) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean exists = false;
        try {
            dao.startOperation();
            exists = dao.objectExists(clz, id);
            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return exists;
    }

    @Override
    public boolean deleteObject(Object object) throws Exception {
        HibernateDataAccess dao = new HibernateDataAccess();
        boolean deleted = false;
        try {
            dao.startOperation();
            dao.removeObject(object);
            deleted = true;
            dao.commit();
        } catch (Exception ex) {
            dao.rollback();
            logger.error("error thrown - ", ex);
            throw new Exception(ex);
        } finally {
            dao.closeSession();
        }
        return deleted;
    }

}

