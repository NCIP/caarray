<tr id="${parameters.id}" style="display:"";">
    <td class="tdLabel"><#rt/>
<#if parameters.label?exists>
    <label <#t/>
<#if parameters.id?exists>
        for="${parameters.id?html}" <#t/>
</#if>
        class="label"<#t/>
    ><#t/>
${parameters.label?html}<#t/>
<#if parameters.required?exists && parameters.required == true>
    <span class="required">*</span><#t/>
</#if>
:<#t/>
</label><#t/>
</#if>
    </td><#lt/>

<td
<#if parameters.align?exists>
    align="${parameters.align?html}"<#t/>
</#if>
><#t/>

<#if parameters.nameValue?exists>
  <@s.property value="parameters.nameValue"/>
</#if>

<input type="text" name="${parameters.name}" value="" tabindex="${parameters.tabindex}" id="${parameters.name}" style="${parameters.cssStyle}"/>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

