/*
 * Copyright Richard Kendall 2020
 *
 * Based on Magic Link example here https://github.com/stianst/keycloak-experimental/blob/master/magic-link/src/main/java/org/keycloak/experimental/magic/MagicLinkFormAuthenticator.java
 *
 */

package com.richardjameskendall.keycloak.auth;

import com.richardjameskendall.ses.EmailSender;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

public class EmailLinkAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger("com.richardjameskendall.keycloak.auth");

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String email = formData.getFirst("email");

        // get config
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();

        // get create user config
        boolean createUsers = config.get("user.create").equals("true");

        // if create user is turned on then first we check the domain suffix

        if(createUsers) {
            logger.info("Create users is enabled.");
            // check email address is from an allowed domain
            String allowedDomains = config.get("email.allowed.suffix");
            logger.info("Allowed domains found: " + allowedDomains);
            List<String> allowedDomainsList = Arrays.asList(allowedDomains.split(","));
            String[] emailAddressBits = email.toLowerCase().split("@");
            String emailSuffix = emailAddressBits[emailAddressBits.length - 1];
            logger.info("Email suffix on request: " + emailSuffix);
            if (!allowedDomainsList.contains(emailSuffix)) {
                logger.info("Email suffix does not match allowed domains");
                context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
            } else {
                logger.info("Email suffix matches an allowed domain");

                // handle user
                UserModel user = context.getSession().users().getUserByEmail(email, context.getRealm());
                if (user == null) {
                    // Register user
                    user = context.getSession().users().addUser(context.getRealm(), email);
                    user.setEnabled(true);
                    user.setEmail(email);
                }

                // generate key and store it
                String key = KeycloakModelUtils.generateId();
                context.getAuthenticationSession().setAuthNote("email-key", key);

                // generate link
                String link = KeycloakUriBuilder.fromUri(context.getRefreshExecutionUrl()).queryParam("key", key).build().toString();

                try {
                    // send email
                    HashMap<String, String> fields = new HashMap<>();
                    fields.put("email", email);
                    fields.put("link", link);
                    EmailSender sender = new EmailSender(config.get("aws.region"));
                    sender.sendWithTemplate(email, config.get("email.from.address"), config.get("email.ses.template"), fields);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                context.setUser(user);
                context.challenge(context.form().createForm("view-email.ftl"));
            }
        } else {
            logger.info("Create users is disabled");
            // lookup user
            UserModel user = context.getSession().users().getUserByEmail(email, context.getRealm());
            if(user == null) {
                logger.info("User does not exist");
                context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
            } else {
                // generate key and store it
                String key = KeycloakModelUtils.generateId();
                context.getAuthenticationSession().setAuthNote("email-key", key);

                // generate link
                String link = KeycloakUriBuilder.fromUri(context.getRefreshExecutionUrl()).queryParam("key", key).build().toString();

                try {
                    // send email
                    HashMap<String, String> fields = new HashMap<>();
                    fields.put("email", email);
                    fields.put("link", link);
                    EmailSender sender = new EmailSender(config.get("aws.region"));
                    sender.sendWithTemplate(email, config.get("email.from.address"), config.get("email.ses.template"), fields);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                context.setUser(user);
                context.challenge(context.form().createForm("view-email.ftl"));
            }
        }
    }

    //@Override
    public void authenticate(AuthenticationFlowContext context) {
        String sessionKey = context.getAuthenticationSession().getAuthNote("email-key");
        if (sessionKey != null) {
            String requestKey = context.getSession().getContext().getUri().getQueryParameters().getFirst("key");
            if (requestKey != null) {
                if (requestKey.equals(sessionKey)) {
                    context.success();
                } else {
                    context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
                }
            } else {
                context.challenge(context.form().createForm("view-email.ftl"));
            }
        } else {
            context.challenge(context.form().createForm("login-email-only.ftl"));
        }
    }

    //@Override
    public boolean requiresUser() {
        return false;
    }

    //@Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    //@Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }

}