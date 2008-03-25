<tr>
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
