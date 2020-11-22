<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
        ${msg("loginTitleHtml",(realm.displayNameHtml!''))?no_esc}
    <#elseif section = "form">
        <#if realm.password>
            <p class="${properties.successHeadingClass!}">Thank you ${auth.attemptedUsername}</p>
            <p class="${properties.successTextClass!}">${(properties.successText)!"Please check your email, you will receive a link to complete your login."}</p>
            <#if properties.showRestartLink??>
                <#if properties.showRestartLink != "no">
                    <p>
                      <a id="reset-login" href="${url.loginRestartFlowUrl}">Start the process again...</a>
                    </p>
                </#if>
            <#else>
                <p>
                  <a id="reset-login" href="${url.loginRestartFlowUrl}">Start the process again...</a>
                </p>
            </#if>
        </#if>
    </#if>
</@layout.registrationLayout>