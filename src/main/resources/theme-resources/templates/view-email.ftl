<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
        ${msg("loginTitleHtml",(realm.displayNameHtml!''))?no_esc}
    <#elseif section = "form">
        <#if realm.password>
            <p>Thank you ${auth.attemptedUsername}</p>
            <p>Please check your email, you will receive a link to complete your login.</p>
            <p>
              <a id="reset-login" href="${url.loginRestartFlowUrl}">Start the process again...</a>
            </p>
        </#if>
    </#if>
</@layout.registrationLayout>