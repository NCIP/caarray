<tr id="${parameters.id}" style="display:none;">
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

<select name="${parameters.name}" tabindex="${parameters.tabindex}" id="${parameters.name}">
<option value="${parameters.headerKey}">${parameters.headerValue}</option>
<@s.iterator value="parameters.list">
    <#assign itemKey = stack.findValue(parameters.listKey)/>
    <#assign itemValue = stack.findString(parameters.listValue)/>
    <option value="${itemKey?html}">${itemValue?html}</option>
</@s.iterator>
</select>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

