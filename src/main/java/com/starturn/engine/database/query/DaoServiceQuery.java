/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.query;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface DaoServiceQuery {
    public boolean saveUpdateEntity(Object object) throws Exception;
    public boolean saveUpdateEntities(List<? extends Object> objects) throws Exception;
    public Object getEntity(Class clz, Serializable id) throws Exception;
    public List getAllEntityRecords(Class clz) throws Exception;
    public boolean checkObjectExists(Class clz, Serializable id) throws Exception;
    public boolean deleteObject(Object object) throws Exception;
}
