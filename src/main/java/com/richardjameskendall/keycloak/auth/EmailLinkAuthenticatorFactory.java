/*
 * Copyright Richard Kendall 2020
 *
 * Based on Magic Link example here https://github.com/stianst/keycloak-experimental/blob/master/magic-link/src/main/java/org/keycloak/experimental/magic/MagicLinkFormAuthenticator.java
 *
 */

package com.richardjameskendall.keycloak.auth;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class EmailLinkAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "magic-form";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        ProviderConfigProperty emailFromAddress;
        emailFromAddress = new ProviderConfigProperty();
        emailFromAddress.setName("email.from.address");
        emailFromAddress.setLabel("Email From Address");
        emailFromAddress.setType(ProviderConfigProperty.STRING_TYPE);
        emailFromAddress.setHelpText("The email address to use when sending emails");
        configProperties.add(emailFromAddress);

        ProviderConfigProperty awsRegion;
        awsRegion = new ProviderConfigProperty();
        awsRegion.setName("aws.region");
        awsRegion.setLabel("AWS Region");
        awsRegion.setType(ProviderConfigProperty.STRING_TYPE);
        awsRegion.setHelpText("The AWS region that emails should be sent from");
        configProperties.add(awsRegion);

        ProviderConfigProperty allowedDomain;
        allowedDomain = new ProviderConfigProperty();
        allowedDomain.setName("email.allowed.suffix");
        allowedDomain.setLabel("Email Suffix");
        allowedDomain.setType(ProviderConfigProperty.STRING_TYPE);
        allowedDomain.setHelpText("The allowed suffix of user email addresses");
        configProperties.add(allowedDomain);

        ProviderConfigProperty sesTemplate;
        sesTemplate = new ProviderConfigProperty();
        sesTemplate.setName("email.ses.template");
        sesTemplate.setLabel("SES Template Name");
        sesTemplate.setType(ProviderConfigProperty.STRING_TYPE);
        sesTemplate.setHelpText("The name of the AWS SES template used to send emails");
        configProperties.add(sesTemplate);

        ProviderConfigProperty createUsers;
        createUsers = new ProviderConfigProperty();
        createUsers.setName("user.create");
        createUsers.setLabel("Create users on demand");
        createUsers.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        createUsers.setDefaultValue("true");
        createUsers.setHelpText("Should users be created if they don't exist? If turned off then domain suffix validation will not work");
        configProperties.add(createUsers);
    }

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    //@Override
    public Authenticator create(KeycloakSession session) {
        return new EmailLinkAuthenticator();
    }

    //@Override
    public String getId() {
        return ID;
    }

    //@Override
    public String getReferenceCategory() {
        return "magic";
    }

    //@Override
    public boolean isConfigurable() {
        return true;
    }

    //@Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    //@Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    //@Override
    public String getDisplayType() {
        return "Magic Link";
    }

    //@Override
    public String getHelpText() {
        return "Magic Link";
    }

    //@Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    //@Override
    public void init(Config.Scope config) {

    }

    //@Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    //@Override
    public void close() {
    }

}