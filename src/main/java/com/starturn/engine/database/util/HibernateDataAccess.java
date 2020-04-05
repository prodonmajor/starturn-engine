/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.util;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Akinwale.Agbaje
 */
public class HibernateDataAccess {
    private Session session;
    private Transaction tx;
    
    public void startOperation() {
        session = HibernateUtil.getSessionFactory().openSession();
        session.doWork((Connection connection) -> {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        });
        tx = session.beginTransaction();
    }
    
    public Session getSession() {
        return session;
    }
    
    public Transaction getTransaction() {
        return tx;
    }
    
    public void createUpdateObject(Object object) {
        session.saveOrUpdate(object);
    }
    
    public Object searchObject(Class clz, Serializable id) {
        Object object = session.get(clz, id);
        return object;
    }
    
    public List searchAll(Class clz) {
        Query query = session.createQuery("FROM " + clz.getName());
        List objects = query.list();
        return objects;
    }
    
    public boolean objectExists(Class clz, Serializable id) {
        Query query = session.createQuery("select count(*) FROM " + clz.getName() + " where id=:id");
        query.setParameter("id", id);
        Long count = (Long) query.uniqueResult();
        return count > 0;
    }
    
    public void removeObject(Object object) {
        session.delete(object);
    }
    
    public void commit() {
        tx.commit();
    }

    public void rollback() {
        if (tx != null) {
            tx.rollback();
        }
    }

    public void closeSession() {
        session.close();
    }
}

