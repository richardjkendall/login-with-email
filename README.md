![Docker Image Size (tag)](https://img.shields.io/docker/image-size/richardjkendall/keycloak-magic/latest)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/richardjkendall/login-with-email/Build%20war%20and%20package%20with%20keycloak%20in%20docker%20image)

# login-with-email

A Keycloak SPI which implements a 'magic-link' solution.  It has support to create users on demand or only allow pre-registered users.

For on-demand creation it checks the email domain against a list of allowed domains.

It is designed to run in AWS environments with access to the Simple Email Sending (SES) service.

Based on this original version: https://github.com/stianst/keycloak-experimental/tree/master/magic-link

## Docker Image

This is available as a docker image layered over the standard jboss/keycloak container.  The way of using the container is the same as the jboss/keycloak image.  https://hub.docker.com/r/jboss/keycloak/

## Usage

0. Setup pre-reqs:

    * Create SES email template (details below)
    * Create appropriate IAM roles to access SES and the template in question

1. Deploy to Keycloak:

    mvn clean install wildfly:deploy

2. Configure realm authentication flow

   * Create copy of Browser flow
   * Delete "Username Password Form" and "OTP Form" executors
   * Click on Actions next to "Copy Of Browser Forms" and click "Add execution"
   * Add "Magic Link"
   * Set requirement "Required" on "Magic Link" executor
   * Click on bindings and switch "Browser flow" to "Copy of browser flow"
   * Configure the executor (mail from address, AWS region, allowed email domains, name of the SES template to use)
   
## SES Template

There's a simple example in this repository (ses-template.json).  The template should contain an {{email}} and a {{link}} field.

## Notes

If you are using SES in the sandbox, you will need to verify any addresses that you send email from.
