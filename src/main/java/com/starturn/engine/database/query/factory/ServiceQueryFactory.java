/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.query.factory;

import com.starturn.engine.database.query.DaoServiceQuery;
import com.starturn.engine.database.query.MemberServiceQuery;
import com.starturn.engine.database.query.impl.DaoServiceQueryImpl;
import com.starturn.engine.database.query.impl.MemberServiceQueryImpl;

/**
 *
 * @author Administrator
 */
public class ServiceQueryFactory {

    public static DaoServiceQuery getDaoServiceQuery() {
        return new DaoServiceQueryImpl();
    }

    public static MemberServiceQuery getMemberServiceQuery() {
        return new MemberServiceQueryImpl();
    }
}
