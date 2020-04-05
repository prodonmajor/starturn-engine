/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.controller.businesslogic.async;

import com.starturn.engine.database.entities.UserToken;
import com.starturn.engine.database.query.DaoServiceQuery;
import com.starturn.engine.database.query.MemberServiceQuery;
import com.starturn.engine.util.GeneralUtility;
import com.starturn.engine.util.notification.thread.EmailAlertHelper;
import com.starturn.engine.util.notification.thread.SmsAlertHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Administrator
 */
@Component
public class Scheduler {

    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);
    private @Autowired
    DaoServiceQuery daoService;
    private @Autowired
    EmailAlertHelper emailAlert;
    private @Autowired
    SmsAlertHelper smsAlert;
    private @Autowired
    MemberServiceQuery memberService;
    private @Autowired
    GeneralUtility util;

    @Scheduled(
            initialDelayString = "${scheduler.traditional.delay.initial}",
            fixedDelayString = "${scheduler.traditional.delay.fixed}"
    )
    public void invalidatePendingTokens() {
        try {
            List<UserToken> pending_tokens = memberService.retrieveValidTokens();
            List<UserToken> expired_tokens = new ArrayList<>();
            int token_validity = Integer.parseInt("${token-validity}");
            if (!pending_tokens.isEmpty()) {
                LOGGER.info("System has found pending tokens to invalidate.");
                pending_tokens.stream().filter((ut) -> (util.getDateDiffInMins(ut.getDateCreated()) > token_validity)).map((ut) -> {
                    ut.setExpired(Boolean.TRUE);
                    return ut;
                }).map((ut) -> {
                    LOGGER.info("the token {}, has reached the expiration time limit", ut.getToken());
                    return ut;
                }).forEach((ut) -> {
                    expired_tokens.add(ut);
                });

                if (!expired_tokens.isEmpty()) {
                    boolean updated = daoService.saveUpdateEntities(expired_tokens);
                    if (updated) {
                        LOGGER.info("System has invalidated the token(s) {}", expired_tokens);
                    } else {
                        LOGGER.info("System was unable to invalidated the token(s) {}", expired_tokens);
                    }
                } else {
                    LOGGER.info("No token found for invalidation yet.");
                }
            }
        } catch (Exception ex) {
            LOGGER.error("error thrown while trying to invalidate tokens due to - ", ex);
        }

    }
}
